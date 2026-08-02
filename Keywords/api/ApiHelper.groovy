package api

import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.annotation.Keyword
import groovy.json.JsonSlurper
import groovy.json.JsonBuilder

/**
 * ApiHelper sebagai:
 *  - PRODUCER : mengirim data (POST/PUT) ke REST API (menciptakan/mengubah resource)
 *  - CONSUMER : mengambil data (GET) dari REST API dan memvalidasi response

 */
class ApiHelper {

    /**
     * PRODUCER: mengirim POST request (membuat resource baru) ke REST API
     * @param endpoint URL endpoint API
     * @param bodyMap  Map yang akan dijadikan JSON body
     * @param headersMap Map header tambahan (opsional)
     */
    @Keyword
    ResponseObject postData(String endpoint, Map bodyMap, Map<String, String> headersMap = [:]) {
        RequestObject request = new RequestObject('Dynamic_POST_' + System.currentTimeMillis())
        request.setRestUrl(endpoint)
        request.setRestRequestMethod('POST')
        request.setHttpHeaderProperties(buildHeaders(headersMap))
        request.setBodyContent(new HttpTextBodyContent(
                new JsonBuilder(bodyMap).toString(), 'UTF-8', 'application/json'))

        ResponseObject response = WS.sendRequest(request)
        return response
    }

    /**
     * PRODUCER: mengirim PUT request (update resource) ke REST API.
     */
    @Keyword
    ResponseObject putData(String endpoint, Map bodyMap, Map<String, String> headersMap = [:]) {
        RequestObject request = new RequestObject('Dynamic_PUT_' + System.currentTimeMillis())
        request.setRestUrl(endpoint)
        request.setRestRequestMethod('PUT')
        request.setHttpHeaderProperties(buildHeaders(headersMap))
        request.setBodyContent(new HttpTextBodyContent(
                new JsonBuilder(bodyMap).toString(), 'UTF-8', 'application/json'))

        return WS.sendRequest(request)
    }

    /**
     * CONSUMER: mengambil data (GET) dari REST API.
     */
    @Keyword
    ResponseObject getData(String endpoint, Map<String, String> headersMap = [:]) {
        RequestObject request = new RequestObject('Dynamic_GET_' + System.currentTimeMillis())
        request.setRestUrl(endpoint)
        request.setRestRequestMethod('GET')
        request.setHttpHeaderProperties(buildHeaders(headersMap))

        return WS.sendRequest(request)
    }

    /**
     * CONSUMER: mengambil data lalu langsung mem-parsing body JSON menjadi Map/List.
     */
    @Keyword
    def getDataAsJson(String endpoint, Map<String, String> headersMap = [:]) {
        ResponseObject response = getData(endpoint, headersMap)
        return new JsonSlurper().parseText(response.getResponseText())
    }

    private List<TestObjectProperty> buildHeaders(Map<String, String> headersMap) {
        List<TestObjectProperty> headers = []
        headers.add(new TestObjectProperty('Content-Type', ConditionType.EQUALS, 'application/json'))
        headersMap.each { k, v ->
            headers.add(new TestObjectProperty(k, ConditionType.EQUALS, v))
        }
        return headers
    }
}
