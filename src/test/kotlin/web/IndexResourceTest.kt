package web

import common.ServerTest
import io.restassured.RestAssured.get
import io.restassured.http.ContentType
import org.hamcrest.Matchers.blankOrNullString
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test

class IndexResourceTest : ServerTest() {

    @Test
    fun testGetIndex() {
        get("/")
            .then()
            .statusCode(200)
            .contentType(ContentType.HTML)
            .body(not(blankOrNullString()))
    }

}