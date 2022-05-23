import os

import requests
import time
import tempfile

sd_url = "http://localhost:8082"

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
        create_envoy(
            instance['node_id'],
            service_id,
            f"sidecar-{instance['node_id']}-{instance['id']}",
            instance['proxy']['monitoring_endpoint']['address'],
            instance['proxy']['monitoring_endpoint']['port'],
            instance['proxy']['ingress_endpoint']['port'],
        )


def move_balancer(service_id, vm_id):
    current_state = requests.get(url=sd_url + "/service", params={"service_id": service_id})


#init_nodes()
# #create_service("1", "vm_1", ["vm_1", "vm_2"], [])
stop_envoy("vm_2", 1025)
