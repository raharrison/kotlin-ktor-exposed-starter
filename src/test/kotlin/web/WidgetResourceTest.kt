package web

import common.ServerTest
import io.restassured.RestAssured.*
import io.restassured.http.ContentType
import model.NewWidget
import model.Widget
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class WidgetResourceTest: ServerTest() {

    @Test
    fun testCreateWidget() {
        // when
        val newWidget = NewWidget(null, "widget1", 12)
        val created = addWidget(newWidget)

        val retrieved = get("/widget/{id}", created.id)
                .then()
                .extract().to<Widget>()

        // then
        assertThat(created.name).isEqualTo(newWidget.name)
        assertThat(created.quantity).isEqualTo(newWidget.quantity)

        assertThat(created).isEqualTo(retrieved)
    }

    @Test
    fun testGetWidgets() {
        // when
        val widget1 = NewWidget(null, "widget1", 10)
        val widget2 = NewWidget(null, "widget2", 5)
        addWidget(widget1)
        addWidget(widget2)

        val widgets = get("/widget")
                .then()
                .statusCode(200)
                .extract().to<List<Widget>>()

        assertThat(widgets).hasSize(2)
        assertThat(widgets).extracting("name").containsExactlyInAnyOrder(widget1.name, widget2.name)
        assertThat(widgets).extracting("quantity").containsExactlyInAnyOrder(widget1.quantity, widget2.quantity)
    }

    @Test
    fun testUpdateWidget() {
        // when
        val widget1 = NewWidget(null, "widget1", 10)
        val saved = addWidget(widget1)

        // then
        val update = NewWidget(saved.id, "updated", 46)
        val updated = given()
                .contentType(ContentType.JSON)
                .body(update)
                .When()
                .put("/widget")
                .then()
                .statusCode(200)
                .extract().to<Widget>()

        assertThat(updated).isNotNull
        assertThat(updated.id).isEqualTo(update.id)
        assertThat(updated.name).isEqualTo(update.name)
        assertThat(updated.quantity).isEqualTo(update.quantity)
    }

    @Test
    fun testDeleteWidget() {
        // when
        val newWidget = NewWidget(null, "widget1", 12)
        val created = addWidget(newWidget)

        // then
        delete("/widget/{id}", created.id)
                .then()
                .statusCode(200)

        get("/widget/{id}", created.id)
                .then()
                .statusCode(404)
    }

    @Nested
    inner class ErrorCases {

        @Test
        fun testUpdateInvalidWidget() {
            val updatedWidget = NewWidget(-1, "invalid", -1)
            given()
                    .contentType(ContentType.JSON)
                    .body(updatedWidget)
                    .When()
                    .put("/widget")
                    .then()
                    .statusCode(404)
        }

        @Test
        fun testDeleteInvalidWidget() {
            delete("/widget/{id}", "-1")
                    .then()
                    .statusCode(404)
        }

        @Test
        fun testGetInvalidWidget() {
            get("/widget/{id}", "-1")
                    .then()
                    .statusCode(404)
        }

    }

    private fun addWidget(widget: NewWidget): Widget {
        return given()
                .contentType(ContentType.JSON)
                .body(widget)
                .When()
                .post("/widget")
                .then()
                .statusCode(201)
                .extract().to()
    }

}