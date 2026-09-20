package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.security.CryptoManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("NER-ResQRoute", appName)
  }

  @Test
  fun `test crypto manager encryption roundtrip`() {
    val plain = "Hazard report: NH-6 impassable due to 400m mudflow"
    val encrypted = CryptoManager.encrypt(plain)
    assertNotNull(encrypted)
    val decrypted = CryptoManager.decrypt(encrypted)
    assertEquals(plain, decrypted)
  }
}
