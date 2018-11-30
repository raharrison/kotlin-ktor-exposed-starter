package service

import common.ServerTest
import kotlinx.coroutines.runBlocking
import model.ChangeType
import model.NewWidget
import model.Notification
import model.Widget
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class WidgetServiceTest: ServerTest() {

    private val widgetService = WidgetService()

    @Test
    fun testAddWidget() = runBlocking {
        // given
        val widget1 = NewWidget(null, "widget1", 10)

        // when
        val saved = addWidget(widget1)

        // then
        val retrieved = widgetService.getWidget(saved.id)
        assertThat(retrieved).isEqualTo(saved)
        assertThat(retrieved?.name).isEqualTo(widget1.name)
        assertThat(retrieved?.quantity).isEqualTo(widget1.quantity)

        Unit
    }

    @Test
    fun testGetAllWidgets() = runBlocking {
        // given
        val widget1 = NewWidget(null, "widget1", 10)
        val widget2 = NewWidget(null, "widget2", 5)
        addWidget(widget1)
        addWidget(widget2)

        // when
        val widgets = widgetService.getAllWidgets()

        // then
        assertThat(widgets).hasSize(2)
        assertThat(widgets).extracting("name").containsExactlyInAnyOrder(widget1.name, widget2.name)
        assertThat(widgets).extracting("quantity").containsExactlyInAnyOrder(widget1.quantity, widget2.quantity)

        Unit
    }

    @Test
    fun testUpdateWidget() = runBlocking {
        // given
        val widget1 = NewWidget(null, "widget1", 10)
        val saved = addWidget(widget1)

        // when
        val update = NewWidget(saved.id, "updated", 46)
        val updated = widgetService.updateWidget(update)

        // then
        assertThat(updated).isNotNull
        assertThat(updated?.id).isEqualTo(update.id)
        assertThat(updated?.name).isEqualTo(update.name)
        assertThat(updated?.quantity).isEqualTo(update.quantity)

        assertThat(widgetService.getWidget(saved.id)).isEqualTo(updated)

        Unit
    }

    @Test
    fun testUpdateWidgetNoIdInserts() = runBlocking {
        // given
        val widget1 = NewWidget(null, "widget1", 10)
        val inserted = widgetService.updateWidget(widget1)

        // then
        assertThat(inserted).isNotNull

        val retrieved = widgetService.getWidget(inserted?.id!!)
        assertThat(retrieved?.name).isEqualTo(widget1.name)
        assertThat(retrieved?.quantity).isEqualTo(widget1.quantity)

        Unit
    }

    @Test
    fun testDeleteWidget() = runBlocking {
        // given
        val widget1 = NewWidget(null, "widget1", 10)
        val saved = addWidget(widget1)

        // when
        assertThat(widgetService.getWidget(saved.id)).isNotNull
        val result = widgetService.deleteWidget(saved.id)

        // then
        assertThat(result).isTrue()
        assertThat(widgetService.getWidget(saved.id)).isNull()
        assertThat(widgetService.getAllWidgets()).isEmpty()
        Unit
    }

    @Nested
    inner class NotificationCases {

        @Test
        fun testNotifyAdd() = runBlocking {
            val widget1 = NewWidget(null, "widget1", 10)

            var called = false
            val func: suspend (Notification<Widget?>) -> Unit = {
                assertThat(it.type).isEqualTo(ChangeType.CREATE)
                assertThat(it.entity?.name).isEqualTo(widget1.name)
                assertThat(it.entity?.quantity).isEqualTo(widget1.quantity)
                called = true
            }

            widgetService.addChangeListener(123, func)
            addWidget(widget1)
            assertThat(called).isTrue()
            Unit
        }

        @Test
        fun testNotifyUpdate() = runBlocking {
            val widget1 = NewWidget(null, "widget1", 10)
            val saved = addWidget(widget1)
            val updated = NewWidget(null, "updated", 25)

            var called = false
            val func: suspend (Notification<Widget?>) -> Unit = {
                assertThat(it.type).isEqualTo(ChangeType.UPDATE)
                assertThat(it.entity?.name).isEqualTo(updated.name)
                assertThat(it.entity?.quantity).isEqualTo(updated.quantity)
                assertThat(it.id).isEqualTo(saved.id)
                called = true
            }

            widgetService.addChangeListener(123, func)
            widgetService.updateWidget(updated.copy(id=saved.id))
            assertThat(called).isTrue()
            Unit
        }

        @Test
        fun testNotifyDelete() = runBlocking {
            val widget1 = NewWidget(null, "widget1", 10)
            val saved = addWidget(widget1)

            var called = false
            val func: suspend (Notification<Widget?>) -> Unit = {
                assertThat(it.type).isEqualTo(ChangeType.DELETE)
                assertThat(it.entity).isNull()
                assertThat(it.id).isEqualTo(saved.id)
                called = true
            }

            widgetService.addChangeListener(123, func)
            widgetService.deleteWidget(saved.id)
            assertThat(called).isTrue()
            Unit
        }

        @Test
        fun testRemoveListener() = runBlocking {
            var called = false
            val func: suspend (Notification<Widget?>) -> Unit = {
                called = true
            }

            widgetService.addChangeListener(123, func)
            widgetService.removeChangeListener(123)
            addWidget(NewWidget(null, "widget1", 10))
            assertThat(called).isFalse()
            Unit
        }

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