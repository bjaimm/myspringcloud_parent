package test.com.herosoft.user.controller;

import com.herosoft.user.controller.UserController;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserControllerTest {

    @Test
    public void testCountTest() {
        UserController userController = new UserController();

        String retValue = userController.countTest();
        assertNotNull(retValue);
        System.out.println("retValue :"+retValue);
    }

    @Test
    public void testThreadMethod() {
        //assertNull("test");
    }
}