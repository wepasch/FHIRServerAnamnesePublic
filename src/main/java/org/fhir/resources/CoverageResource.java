package org.fhir.resources;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.List;

import static org.utility.Utilities.getJsonAsString;
import static org.utility.Utilities.makeJsonPretty;

public class CoverageResource extends FhirResource {
    private static final Logger LOGGER = LogManager.getLogger(CoverageResource.class);
    public static final List<String> resourceFields = List.of(
            "id", "stautus", "subscriberid", "beneficiary", "payor");
    private String id;
    private String status;
    private String subscriberId;
    private String beneficiary;
    private String payor;

    public CoverageResource(String[] array) {
        if (array.length != 5) {
            LOGGER.error("Array from database has '{}' and not 5 items.", array.length);
            return;
        }
        this.id = array[0];
        this.status = array[1];
        this.subscriberId = array[2];
        this.beneficiary = array[3];
        this.payor = array[4];
    }

    @Override
    public String getJsonObjectString() {
        JSONObject jsonObject = new JSONObject(getJsonAsString("blankCoverage"));
        jsonObject.put("id", this.id);
        jsonObject.put("status", this.status);
        jsonObject.put("subscriberId", this.subscriberId);
        jsonObject.getJSONObject("beneficiary").put("reference", this.beneficiary);
        jsonObject.getJSONArray("payor").getJSONObject(0).put("reference", this.payor);
        return makeJsonPretty(jsonObject.toString());
    }
}
