package org.shadownova.vollzanbel

import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.shadownova.vollzanbel.client.ApiReference
import org.shadownova.vollzanbel.client.Dnd5eApiClient
import org.shadownova.vollzanbel.controller.ReferenceCatalogController

class ReferenceCatalogControllerTest {
    private val client = mock(Dnd5eApiClient::class.java)
    private val controller = ReferenceCatalogController(client)

    @Test
    fun allowedIdentityCatalogIsForwarded() {
        val expected = listOf(ApiReference("wizard", "Wizard", "/api/2014/classes/wizard"))
        `when`(client.getReferenceList("classes")).thenReturn(expected)

        assertEquals(expected, controller.list("classes").body)
    }

    @Test
    fun arbitraryProxyResourcesAreRejected() {
        assertEquals(404, controller.list("https://example.com").statusCode.value())
    }
}
