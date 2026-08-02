import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

import api.ApiHelper

/*
 * Studi kasus: Swagger Petstore (OpenAPI resmi - https://petstore.swagger.io/v2/swagger.json)
 * Katalon berperan sebagai:
 *   - PRODUCER : POST /pet   (membuat data pet baru)
 *                PUT  /pet   (update data pet)
 *   - CONSUMER : GET  /pet/{petId} (mengambil & memvalidasi data pet)
 */

ApiHelper apiHelper = new ApiHelper()
String baseUrl = 'https://petstore.swagger.io/v2/pet'

// generate id unik supaya tidak bentrok antar-run
long petId = System.currentTimeMillis().intdiv(1000)

// STEP 1 - PRODUCER: create data (POST /pet)
Map newPet = [
        id       : petId,
        category : [id: 1, name: 'Dogs'],
        name     : 'Doggie',
        photoUrls: ['https://example.com/dog.jpg'],
        tags     : [[id: 1, name: 'friendly']],
        status   : 'available'
]

ResponseObject postResponse = apiHelper.postData(baseUrl, newPet)

WS.verifyResponseStatusCode(postResponse, 200)
def createdPet = new groovy.json.JsonSlurper().parseText(postResponse.getResponseText())
assert createdPet.name == newPet.name
assert createdPet.status == 'available'
println("PRODUCER - Pet berhasil dibuat dengan id: " + createdPet.id)

// STEP 2 - CONSUMER: read data (GET /pet/{petId})
String getUrl = baseUrl + '/' + petId
ResponseObject getResponse = apiHelper.getData(getUrl)

WS.verifyResponseStatusCode(getResponse, 200)
def fetchedPet = new groovy.json.JsonSlurper().parseText(getResponse.getResponseText())

assert fetchedPet.id == petId
assert fetchedPet.name == 'Doggie'
assert fetchedPet.status == 'available'
println("CONSUMER - Pet berhasil diambil: " + fetchedPet)

// STEP 3 - PRODUCER: update data (PUT /pet)
Map updatePet = [
        id       : petId,
        category : [id: 1, name: 'Dogs'],
        name     : 'Doggie Updated',
        photoUrls: ['https://example.com/dog.jpg'],
        tags     : [[id: 1, name: 'friendly']],
        status   : 'sold'
]

ResponseObject putResponse = apiHelper.putData(baseUrl, updatePet)
WS.verifyResponseStatusCode(putResponse, 200)
def updatedPet = new groovy.json.JsonSlurper().parseText(putResponse.getResponseText())
assert updatedPet.status == 'sold'
println("PRODUCER - Pet berhasil diupdate, status baru: " + updatedPet.status)


// STEP 4 - CONSUMER: verifikasi ulang setelah update (GET /pet/{petId})
ResponseObject verifyResponse = apiHelper.getData(getUrl)
WS.verifyResponseStatusCode(verifyResponse, 200)
def verifiedPet = new groovy.json.JsonSlurper().parseText(verifyResponse.getResponseText())
assert verifiedPet.status == 'sold'
assert verifiedPet.name == 'Doggie Updated'
println("CONSUMER - Verifikasi data setelah update berhasil: " + verifiedPet)
