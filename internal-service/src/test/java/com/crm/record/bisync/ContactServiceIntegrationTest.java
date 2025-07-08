package com.crm.record.bisync;

import com.crm.record.bisync.constants.Constants;
import com.crm.record.bisync.model.ContactResponse;
import com.crm.record.bisync.service.ContactService;
import com.crm.record.bisync.dao.ContactDao;
import com.crm.record.bisync.model.OperationType;

import org.junit.jupiter.api.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ContactServiceIntegrationTest {

    @Autowired
    private ContactService contactService;

    @Autowired
    private ContactDao contactDao;

    @BeforeEach
    public void setup() {
        contactDao.clear();
    }

    @Test
    @Order(1)
    public void testCreateContact_Success() {
        String sampleRequestBody = "{\"AccountId\":\"12345\",\"FirstName\":\"Mridul\",\"LastName\":\"Puri\",\"Email\":\"mridul.puri@email.com\",\"Phone\":\"1234567890\",\"MailingAddress\":{\"Street\":\"123 St\",\"City\":\"Bangalore\",\"State\":\"Karnataka\",\"PostalCode\":\"7654\",\"Country\":\"India\"}}";
        List<String> requestBodies = Collections.singletonList(sampleRequestBody);

        List<ContactResponse> responses = contactService.processContacts(
                Constants.CRM1, OperationType.CREATE, requestBodies, null, null, null, null
        );

        Assertions.assertEquals(1, responses.size());
        Assertions.assertTrue(responses.get(0).getMessage().equals("Contact created successfully"));
    }

    @Test
    @Order(2)
    public void testCreateContact_SchemaValidationFailure() {
        String invalidRequestBody = "{\"firstName\":123}";
        List<String> requestBodies = Collections.singletonList(invalidRequestBody);

        List<ContactResponse> responses = contactService.processContacts(
                Constants.CRM1, OperationType.CREATE, requestBodies, null, null, null, null
        );

        Assertions.assertEquals(1, responses.size());
        Assertions.assertTrue(responses.get(0).getMessage().contains("Schema validation failed"));
    }

    @Test
    @Order(3)
    public void testGetContact_Success() {
        List<String> requestBodies = Collections.singletonList("{\"AccountId\":\"1\",\"FirstName\":\"Mridul\",\"LastName\":\"Puri\",\"Email\":\"mridul.puri@email.com\",\"Phone\":\"1234567890\",\"MailingAddress\":{\"Street\":\"123 St\",\"City\":\"Bangalore\",\"State\":\"Karnataka\",\"PostalCode\":\"7654\",\"Country\":\"India\"}}");

        contactService.processContacts(Constants.CRM1, OperationType.CREATE, requestBodies, null, null, null, null);

        List<ContactResponse> responses = contactService.processContacts(
                Constants.CRM1, OperationType.GET, null, null, null, "1", null
        );

        Assertions.assertEquals(1, responses.size());
        Assertions.assertTrue(responses.get(0).getMessage().contains("Mridul"));
    }

    @Test
    @Order(4)
    public void testUpdateContact_Success() throws Exception {
        String sampleRequestBody = "{\"vid\":32145,\"properties\":{\"firstname\":{\"value\":\"Mridul\"},\"lastname\":{\"value\":\"Puri\"},\"email\":{\"value\":\"mridul.puri@email.com\"},\"phone\":{\"value\":\"1234567890\"},\"address_street\":{\"value\":\"123 St\"},\"address_city\":{\"value\":\"Bangalore\"},\"address_state\":{\"value\":\"KA\"},\"address_postalcode\":{\"value\":\"42344\"},\"address_country\":{\"value\":\"India\"}}}";
        List<String> requestBodies = Collections.singletonList(sampleRequestBody);

        contactService.processContacts(Constants.CRM2, OperationType.CREATE, requestBodies, null, null, null, null);

        sampleRequestBody.replace("Mridul", "Maanik");

        List<String> updateRequestBodies = Collections.singletonList(sampleRequestBody);

        List<ContactResponse> responses = contactService.processContacts(
                Constants.CRM2, OperationType.UPDATE, updateRequestBodies, null, null, null, null
        );

        Assertions.assertEquals(1, responses.size());
        Assertions.assertTrue(responses.get(0).getMessage().equals("Contact updated successfully"));
    }

    @Test
    @Order(5)
    public void testDeleteContact_Success() {
        List<String> requestBodies = Collections.singletonList("{\"id\":\"12345\",\"First_Name\":\"Mridul\",\"Last_Name\":\"Puri\",\"Email\":\"mridul.puri@email.com\",\"Phone\":\"1234567890\",\"Mailing_Street\":\"123 St\",\"Mailing_City\":\"Bangalore\",\"Mailing_State\":\"KA\",\"Mailing_Zip\":\"343143\",\"Mailing_Country\":\"India\"}");

        contactService.processContacts("CRM3", OperationType.CREATE, requestBodies, null, null, null, null);

        List<ContactResponse> responses = contactService.processContacts(
                "CRM3", OperationType.DELETE, null, null, null, null, Collections.singletonList("12345")
        );

        Assertions.assertEquals(1, responses.size());
        Assertions.assertTrue(responses.get(0).getMessage().contains("Contact deleted successfully"));
    }
}
