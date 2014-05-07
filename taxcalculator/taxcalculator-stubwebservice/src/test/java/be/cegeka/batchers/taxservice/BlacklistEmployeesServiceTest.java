package be.cegeka.batchers.taxservice;

import org.fest.assertions.api.Assertions;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: raduci
 * Date: 07.05.2014
 * Time: 10:34
 * To change this template use File | Settings | File Templates.
 */
public class BlacklistEmployeesServiceTest {

    @Test
    public void testBlacklistEmployeesAreRegistedAsSuch(){
        BlacklistEmployeesService blacklistEmployeesService = new BlacklistEmployeesService();
        String blacklistEmplyeeID1 = "123456";
        String blacklistEmplyeeID2 = "aaa";
        String blacklistEmplyeeID3 = "ggfdsr";
        blacklistEmployeesService.blacklistEmployeesProperty =  blacklistEmplyeeID1 + "," + blacklistEmplyeeID2 + "," +   blacklistEmplyeeID3;
        blacklistEmployeesService.parseBlacklistEmployees();

        Assertions.assertThat(blacklistEmployeesService.isEmployeeBlacklisted(blacklistEmplyeeID1)).isTrue();
        Assertions.assertThat(blacklistEmployeesService.isEmployeeBlacklisted(blacklistEmplyeeID2)).isTrue();
        Assertions.assertThat(blacklistEmployeesService.isEmployeeBlacklisted(blacklistEmplyeeID3)).isTrue();
    }
}
