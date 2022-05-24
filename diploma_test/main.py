import argparse
import os
from re import sub

import requests

sd_url = "http://localhost:808s"

with open('config_template.yaml', 'r') as f:
    config_template = f.read()


def init_nodes():
    node_ips = ["192.168.56.2", "192.168.56.3", "192.168.56.4"]
    node_ids = ["vm_1", "vm_2", "vm_3"]
    for ip, node_id in zip(node_ips, node_ids):
        data = {
            "node": {
                "node_identifier": node_id,
                "available_addresses": [
                  ip
                ]
            }
        }
        response = requests.post(url=sd_url + "/node", json=data)
        print(response.json())


def create_envoy(vm_id, cluster_id, node_id, admin_ip, admin_port, ingress_port):
    base_id = ingress_port
    template = config_template % {"cluster_id": cluster_id,
                                  "node_id": node_id,
                                  "admin_ip": admin_ip,
                                  "admin_port": admin_port
                                  }
    tmp_file_name = f'{base_id}.yaml'
    with open(tmp_file_name, 'w') as tmp_file:
        tmp_file.write(template)

    os.system(f"vagrant scp {tmp_file_name} {vm_id}:/home/vagrant/{base_id}.yaml")
    os.system(f"ssh {vm_id} -fn 'nohup envoy -c {base_id}.yaml --base-id {base_id} >/dev/null 2>&1 &'")

    os.remove(tmp_file_name)


def stop_envoy(vm_id, base_id):
    os.system(f"ssh {vm_id} 'kill -9 `ps aux | grep \"[c] {base_id}.yaml\" | awk -F \" \" '\"'\"'{{print $2}}'\"'\"' | head -n 1`'")


def create_service(service_id, balancer_vm_id, instances_vm_ids, used_services):
    data = {
        "service_id": service_id,
        "service_name": "some service",
        "service_ingress_node_id": balancer_vm_id,
        "service_instance_ids": [{"id": f'{i + 1}', "node_id": node_id} for i, node_id in enumerate(instances_vm_ids)],
        "used_services": used_services,
        "port": 8080
    }
    response = requests.post(url=sd_url + "/service", json=data).json()
    monitoring_endpoint = response['service_ingress_proxy']['monitoring_endpoint']
    print("Creating balancer envoy")
    create_envoy(
        balancer_vm_id,
        service_id,
        "balancer-" + balancer_vm_id,
        monitoring_endpoint['address'],
        monitoring_endpoint['port'],
        response['service_ingress_proxy']['ingress_endpoint']['port']
    )

    for instance in response['instances']:
        print(f"creating sidecar on {instance['node_id']}")
        create_instance(service_id, instance)


def move_balancer(service_id, vm_id):
    current_state = requests.get(url=sd_url + "/service", params={"service_id": service_id}).json()

    move_balancer_response = requests.put(url=sd_url + "/service/move/balancer", params={"service_id": service_id, "to_nod_id": vm_id}).json()
    
    stop_envoy(current_state['service_ingress_proxy']['node_id'], current_state['service_ingress_proxy']['ingress_endpoint']['port'])
    create_envoy(
        move_balancer_response['service_ingress_proxy']['node_id'], 
        service_id,
        f"balancer-{move_balancer_response['service_ingress_proxy']['node_id']}",
        move_balancer_response["service_ingress_proxy"]['monitoring_endpoint']['address'],
        move_balancer_response["service_ingress_proxy"]['monitoring_endpoint']['port']
    )


def stop_instance(instance):
    stop_envoy(instance['node_id'], instance['proxy']['ingress_endpoint']['port'])


def create_instance(service_id, instance):
    create_envoy(
        instance['node_id'],
        service_id,
        f"sidecar-{instance['id']}-{instance['node_id']}",
        instance['proxy']['monitoring_endpoint']['address'],
        instance['proxy']['monitoring_endpoint']['port'],
        instance['proxy']['ingress_endpoint']['port']
    )


def move_instance(service_id, from_instance_id, from_vm_id, to_instance_id, to_vm_id):
    current_state = requests.get(url=sd_url + "/service", params={"service_id": service_id}).json()
    instance = list(filter(lambda inst: inst['id'] == from_instance_id and inst['node_id'] == from_vm_id, current_state['instances']))[0]
    move_request = {
        "service_id": service_id,
        "from_service_instance_id": {
            "id": from_instance_id,
            "node_id": from_vm_id
        },
        "to_service_instance_id": {
            "id": to_instance_id,
            "node_id": to_vm_id
        }
    }
    move_instance_response = requests.put(url=sd_url + "/service/instance/move", json=move_request).json()

    new_instance = list(filter(lambda inst: inst['id'] == to_instance_id and inst['node_id'] == to_vm_id, move_instance_response['instances']))[0]

    print("stopping envoy")

    stop_instance(instance)
    print("creating new envoy")
    create_instance(new_instance)


def add_instance(service_id, instance_id, vm_id):
    add_instance_response = requests.put(url=sd_url + "/service/instance/add", json={"service_id": service_id, "service_instance_ids": [{"id": instance_id, "node_id": vm_id}]}).json()
    new_instance = list(filter(lambda inst: inst['id'] == instance_id and inst['node_id'] == vm_id, add_instance_response['instances']))[0]
    create_instance(service_id, new_instance)
    

def delete_instance(service_id, instance_id, vm_id):
    current_state = requests.get(url=sd_url + "/service", params={"service_id": service_id}).json()
    instance_to_delete = list(filter(lambda inst: inst['id'] == instance_id and inst['node_id'] == vm_id, current_state['instances']))[0]
    requests.delete(url=sd_url + "/service/instance/delete", json={"service_id": service_id, "service_instance_ids": [{"id": instance_id, "node_id": vm_id}]})
    stop_instance(instance_to_delete)


def main(args):
    print(args)

if __name__ == '__main__':

    parser = argparse.ArgumentParser()
    parser.add_argument("--service-id", type=str, help="service id", dest='service_id')

    sub_parsers = parser.add_subparsers(help='sub-command help')

    create_service_parser = sub_parsers.add_parser('create-service', help='Create new service command')
    create_service_parser.add_argument("--service-node-ids", dest='service_node_ids', action='append', help='instances node ids. Repeatable', required=True)
    create_service_parser.add_argument("--balancer-node-id", dest='balancer_node_id', type=str, help='Balancer node id', required=True)

    move_balancer_parser = sub_parsers.add_parser('move-balancer', help='Move service balancer')
    move_balancer_parser.add_argument("--balancer-node-id", dest='balancer_node_id', type=str, help='New balancer node id', required=True)

    move_instance_parser = sub_parsers.add_parser('move-instance', help='Move service instance')
    move_instance_parser.add_argument("--from-instance-id", dest='from_instance_id', type=str, help='Id of instance to move', required=True)
    move_instance_parser.add_argument("--from-node-id", dest='from_node_id', type=str, help='Node id of instance to move', required=True)
    move_instance_parser.add_argument("--to-instance-id", dest='to_instance_id', type=str, help='New id of instance to move', required=True)
    move_instance_parser.add_argument("--to-node-id", dest='to_node_id', type=str, help='New node id of instance to move', required=True)


    add_instance_parser = sub_parsers.add_parser('add-instance', help='Add instance to service')
    add_instance_parser.add_argument("--instance-id", dest='instance_id', help='Id of new instance', required=True)
    add_instance_parser.add_argument("--node-id", dest="node_id", help="Node id of new instance", required=True)


    delete_instance_parser = sub_parsers.add_parser('delete-instance', help='Delete instance of service')
    delete_instance_parser.add_argument("--instance-id", dest='instance_id', help='Id of instance to delete', required=True)
    delete_instance_parser.add_argument("--node-id", dest="node_id", help="Node id of instance to delete", required=True)

    init_nodes_parser = sub_parsers.add_parser('init-nodes', help='Initiate nodes')
    main(parser.parse_args())
#init_nodes()
# #create_service("1", "vm_1", ["vm_1", "vm_2"], [])
# stop_envoy("vm_2", 1025)
