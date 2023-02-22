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
package com.afollestad.assent.internal

import android.content.Context
import androidx.fragment.app.Fragment
import com.afollestad.assent.AssentResult
import com.afollestad.assent.internal.Assent.Companion.ensureFragment
import com.afollestad.assent.internal.Assent.Companion.forgetFragment
import com.afollestad.assent.internal.Assent.Companion.get

/** @author Aidan Follestad (afollestad) */
class PermissionFragment : androidx.fragment.app.Fragment() {

//  override fun onAttach(context: Context?) {
//    super.onAttach(context)
//  }

  override fun onDetach() {
    super.onDetach()
  }

  internal fun perform(request: PendingRequest) {
    this.requestPermissions(request.permissions.allValues(), request.requestCode)
  }

  internal fun detach() {
    if (parentFragment != null) {
      parentFragment?.transact {
        detach(this@PermissionFragment)
        remove(this@PermissionFragment)
      }
    } else if (activity != null) {
      activity?.transact {
        detach(this@PermissionFragment)
        remove(this@PermissionFragment)
      }
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    onPermissionsResponse(
        permissions = permissions,
        grantResults = grantResults
    )
  }
}

internal fun androidx.fragment.app.Fragment.onPermissionsResponse(
  permissions: Array<out String>,
  grantResults: IntArray
) = synchronized(Assent.LOCK) {
  val currentRequest = get().currentPendingRequest
  if (currentRequest == null) {
    return@synchronized
  }

  if (currentRequest.permissions.equalsStrings(permissions)) {
    // Execute the response
    val result = AssentResult(
        permissions = permissions.toPermissions(),
        grantResults = grantResults
    )
    currentRequest.callbacks.invokeAll(result)
    get().currentPendingRequest = null
  } else {
    return@synchronized
  }

  if (get().requestQueue.isNotEmpty()) {
    // Execute the next request in the queue
    val nextRequest = get().requestQueue.pop()
    get().currentPendingRequest = nextRequest
    ensureFragment(this@onPermissionsResponse).perform(nextRequest)
  } else {
    // No more requests to execute, we can destroy the Fragment
    forgetFragment()
  }
}
