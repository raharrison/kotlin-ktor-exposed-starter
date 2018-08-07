package service

import common.ServerTest
import kotlinx.coroutines.experimental.runBlocking
import model.NewWidget
import model.Widget
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class WidgetServiceTest: ServerTest() {

    private val widgetService = WidgetService()

    @Test
    fun testAddWidget() = runBlocking {
        // when
        val widget1 = NewWidget(null, "widget1", 10)

        // then
        val saved = addWidget(widget1)

        // verify
        val retrieved = widgetService.getWidget(saved.id)
        assertThat(retrieved).isEqualTo(saved)
        assertThat(retrieved?.name).isEqualTo(widget1.name)
        assertThat(retrieved?.quantity).isEqualTo(widget1.quantity)

        Unit
    }

    @Test
    fun testGetAllWidgets() = runBlocking {
        // when
        val widget1 = NewWidget(null, "widget1", 10)
        val widget2 = NewWidget(null, "widget2", 5)
        addWidget(widget1)
        addWidget(widget2)

        // then
        val widgets = widgetService.getAllWidgets()

        assertThat(widgets).hasSize(2)
        assertThat(widgets).extracting("name").containsExactlyInAnyOrder(widget1.name, widget2.name)
        assertThat(widgets).extracting("quantity").containsExactlyInAnyOrder(widget1.quantity, widget2.quantity)

        Unit
    }

    @Test
    fun testUpdateWidget() = runBlocking {
        // when
        val widget1 = NewWidget(null, "widget1", 10)
        val saved = addWidget(widget1)

        // then
        val update = NewWidget(saved.id, "updated", 46)
        val updated = widgetService.updateWidget(update)

        assertThat(updated).isNotNull
        assertThat(updated?.id).isEqualTo(update.id)
        assertThat(updated?.name).isEqualTo(update.name)
        assertThat(updated?.quantity).isEqualTo(update.quantity)

        assertThat(widgetService.getWidget(saved.id)).isEqualTo(updated)

        Unit
    }

    @Test
    fun testUpdateWidgetNoIdInserts() = runBlocking {
        // when
        val widget1 = NewWidget(null, "widget1", 10)
        val inserted = widgetService.updateWidget(widget1)

        assertThat(inserted).isNotNull

        val retrieved = widgetService.getWidget(inserted?.id!!)
        assertThat(retrieved?.name).isEqualTo(widget1.name)
        assertThat(retrieved?.quantity).isEqualTo(widget1.quantity)

        Unit
    }

    @Test
    fun testDeleteWidget() = runBlocking {
        // when
        val widget1 = NewWidget(null, "widget1", 10)
        val saved = addWidget(widget1)

        // then
        assertThat(widgetService.getWidget(saved.id)).isNotNull
        val result = widgetService.deleteWidget(saved.id)

        // verify
        assertThat(result).isTrue()
        assertThat(widgetService.getWidget(saved.id)).isNull()
        assertThat(widgetService.getAllWidgets()).isEmpty()
        Unit
    }

    @Nested
    inner class ErrorCases {

        @Test
        fun testUpdateInvalidWidget() = runBlocking {
            assertThat(widgetService.updateWidget(NewWidget(-1, "invalid", -1))).isNull()
        }

        @Test
        fun testGetInvalidWidget() = runBlocking {
            assertThat(widgetService.getWidget(-1)).isNull()
        }

        @Test
        fun testDeleteInvalidWidget() = runBlocking {
            assertThat(widgetService.deleteWidget(-1)).isFalse()
            Unit
        }
    }

    private suspend fun addWidget(widget: NewWidget): Widget {
        return widgetService.addWidget(widget)
    }
}