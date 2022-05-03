import java.net.InetAddress;
import java.net.UnknownHostException;

import edu.phystech.servicemesh.model.LocalAddressLayout;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.util.Assert;

import static edu.phystech.servicemesh.model.LocalAddressLayout.convertAddressToInt;
import static edu.phystech.servicemesh.model.LocalAddressLayout.convertIntToAddress;

public class AddressConversionTest {
    @ParameterizedTest
    @CsvSource(value = {
            "0.0.0.0,0",
            "127.0.0.1,2130706433",
            "127.255.255.255,2147483647",
    })
    public void testConversionToInt(String address, int expectedInt) throws UnknownHostException {
        Assertions.assertEquals(expectedInt, convertAddressToInt(InetAddress.getByName(address)));
        Assertions.assertEquals(address, convertIntToAddress(expectedInt));
    }
}
