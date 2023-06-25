package org.fhir.resources;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import static org.utility.Utilities.getJsonAsString;
import static org.utility.Utilities.makeJsonPretty;

public class OrganizationResource extends FhirResource {
    private static final Logger LOGGER = LogManager.getLogger(OrganizationResource.class);
    public static final List<String> resourceFields = List.of(
            "id", "name", "active");
    public static final HashMap DB_ORGANIZATION_COL_NAME = new HashMap() {{
        put(0, "id");
        put(1, "name");
        put(2, "active");
    }};

    private String id;
    private String name;

    public OrganizationResource(String[] array) {
        if (array.length != 3) {
            LOGGER.error("Array from database has '{}' and not 3 items.", array.length);
            return;
        }
        this.name = array[1];
        this.id = array[0];
    }

    @Override
    public String getJsonObjectString() {
        JSONObject jsonObject = new JSONObject(getJsonAsString("blankOrganization"));
        jsonObject.put("id", this.id);
        jsonObject.put("name", this.name);
        return makeJsonPretty(jsonObject.toString());
    }
}
