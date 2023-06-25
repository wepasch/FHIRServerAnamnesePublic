package org.fhir.resources;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.utility.Utilities.makeJsonPretty;

public class PatientResource extends FhirResource {
    private static final Logger LOGGER = LogManager.getLogger(PatientResource.class);
    public static final Map<Integer, String> DB_PATIENT_COL_NAME = new HashMap() {{
        put(0, "id");
        put(1, "last_updated");
        put(2, "surname");
        put(3, "name");
        put(4, "birthdate");
        put(5, "gender");
        put(6, "phone_number");
        put(7, "email");
        put(8, "address_line");
        put(9, "address_postal_code");
        put(10, "address_city");
        put(11, "active");
    }};

    public static final List<String> resourceFields = List.of(
            "id", "meta.lastUpdated", "name.family", "name.given", "birthDate", "gender",
            "phone", "mail", "address.line", "address.postalCode", "address.city", "active");

    private String id;
    private String[] name;
    private String surname;
    private String birthDate;
    private String gender;
    private String[] address;
    private String phoneNumber;
    private String email;
    private String lastUpdated;
    private Boolean active;

    public PatientResource(String[] databaseArray) {
        if (databaseArray.length != 12) {
            LOGGER.error("Array from database has '{}' and not 12 items.", databaseArray.length);
            return;
        }
        this.id = databaseArray[0];
        this.lastUpdated = databaseArray[1];
        this.surname = databaseArray[2];
        this.name = databaseArray[3].split("\\s+");
        this.birthDate = databaseArray[4];
        this.gender = databaseArray[5];
        this.phoneNumber = databaseArray[6];
        this.email = databaseArray[7];
        this.address = new String[4];
        this.address[1] = databaseArray[8];
        this.address[3] = databaseArray[9];
        this.address[2] = databaseArray[10];
        this.active = databaseArray[11].equals("t");

        this.address[0] = this.address[1] +
                " " +
                this.address[2] +
                " " +
                this.address[3];
    }

    @Override
    public String getJsonObjectString() {
        JSONObject jsonObject = new JSONObject();
        JSONObject metaOb = new JSONObject();
        metaOb.put("lastUpdated", this.lastUpdated);
        JSONArray nameArray = new JSONArray();
        nameArray.put(new JSONObject().put("family", this.surname));
        nameArray.put(new JSONObject().put("given", name));
        JSONObject phoneOb = (new JSONObject()).put("system", "phone");
        phoneOb.put("value", this.phoneNumber);
        JSONObject mailOb = (new JSONObject()).put("system", "email");
        mailOb.put("value", this.email);
        JSONArray telArray = new JSONArray();
        telArray.put(phoneOb);
        telArray.put(mailOb);
        JSONObject addressOb = new JSONObject();
        JSONArray addressLineArray = new JSONArray();
        addressLineArray.put(this.address[1]);
        addressOb.put("text", this.address[0]);
        addressOb.put("line", addressLineArray);
        addressOb.put("postalCode", this.address[2]);
        addressOb.put("city", this.address[3]);
        JSONArray addressArray = new JSONArray();
        addressArray.put(addressOb);

        jsonObject.put("active", this.active);
        jsonObject.put("address", addressArray);
        jsonObject.put("telecom", telArray);
        jsonObject.put("gender", this.gender);
        jsonObject.put("birthDate", this.birthDate);
        jsonObject.put("name", nameArray);
        jsonObject.put("meta", metaOb);
        jsonObject.put("id", this.id);
        jsonObject.put("resourceType", "Patient");

        return makeJsonPretty(jsonObject.toString());

    }

}
