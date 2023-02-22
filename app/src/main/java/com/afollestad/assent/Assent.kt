/**
 * Designed and developed by Aidan Follestad (@afollestad)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.afollestad.assent

import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.annotation.CheckResult
import androidx.core.content.ContextCompat
import com.afollestad.assent.internal.Assent.Companion.LOCK
import com.afollestad.assent.internal.Assent.Companion.get
import com.afollestad.assent.internal.PendingRequest
import com.afollestad.assent.internal.PermissionFragment
import com.afollestad.assent.internal.equalsPermissions
import com.afollestad.assent.rationale.RationaleHandler

/**
 * Returns true if ALL given [permissions] have been granted.
 */
@CheckResult
fun Context.isAllGranted(vararg permissions: Permission): Boolean {
  return permissions.all {
    ContextCompat.checkSelfPermission(
        this, it.value
    ) == PERMISSION_GRANTED
  }
}

internal fun <T : Any> T.startPermissionRequest(
  attacher: (T) -> PermissionFragment,
  permissions: Array<out Permission>,
  requestCode: Int = 20,
  rationaleHandler: RationaleHandler? = null,
  callback: Callback
) = synchronized(LOCK) {
  log("askForPermissions(${permissions.joinToString()})")

  if (rationaleHandler != null) {
    rationaleHandler.requestPermissions(permissions, requestCode, callback)
    return
  }

  val currentRequest = get().currentPendingRequest
  if (currentRequest != null &&
      currentRequest.permissions.equalsPermissions(*permissions)
  ) {
    // Request matches permissions, append a callback
    log("Callback appended to existing matching request")
    currentRequest.callbacks.add(callback)
    return@synchronized
  }

  // Create a new pending request since none exist for these permissions
  val newPendingRequest = PendingRequest(
      permissions = permissions.toList(),
      requestCode = requestCode,
      callbacks = mutableListOf(callback)
  )

  if (currentRequest == null) {
    // There is no active request so we can execute immediately
    get().currentPendingRequest = newPendingRequest
    log("New request, performing now")
    attacher(this).perform(newPendingRequest)
  } else {
    // There is an active request, append this new one to the queue
    if (currentRequest.requestCode == requestCode) {
      newPendingRequest.requestCode = requestCode + 1
    }
    log("New request queued for when the current is complete")
    get().requestQueue += newPendingRequest
  }
}

internal fun Any.log(message: String) {
}

private fun Any.name() = this::class.java.simpleName
