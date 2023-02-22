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
@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.afollestad.assent

import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.annotation.CheckResult
import com.afollestad.assent.internal.containsPermission

/**
 * Wraps a result for a permission request, which provides utility
 * methods and is sent through callbacks.
 *
 * @author Aidan Follestad (afollestad)
 */
data class AssentResult(
  val permissions: List<Permission>,
  val grantResults: IntArray
) {
  init {
    require(permissions.size == grantResults.size) {
      "Permissions and grant results sizes should match."
    }
  }

  /** Returns true if this result contains the given permission. */
  @CheckResult
  fun containsPermissions(permission: Permission) =
    this.permissions.containsPermission(permission)

  @CheckResult fun isAllGranted(permissions: List<Permission>): Boolean {
    for (perm in permissions) {
      val index = this.permissions.indexOfFirst { it.value == perm.value }
      require(index != -1) { "Permission ${perm.name} doesn't exist in this result set." }
      val granted = this.grantResults[index] == PERMISSION_GRANTED
      if (!granted) return false
    }
    return true
  }

  /**
   * If no parameters are given, returns true if all permissions in the request were granted.
   *
   * If parameters are given, returns true if all given [permissions] were granted.
   */
  @CheckResult fun isAllGranted(vararg permissions: Permission): Boolean {
    if (permissions.isEmpty()) {
    }
    return isAllGranted(permissions.toList())
  }

  /** Returns true if all permissions in the given array have been denied. */
  @CheckResult fun isAllDenied(vararg permissions: Permission): Boolean {
    for (perm in permissions) {
      val index = this.permissions.indexOfFirst { it.value == perm.value }
      require(index != -1) { "Permission ${perm.name} doesn't exist in this result set." }
      val granted = this.grantResults[index] == PERMISSION_DENIED
      if (!granted) return false
    }
    return true
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as AssentResult

    if (permissions != other.permissions) return false
    if (!grantResults.contentEquals(other.grantResults)) return false

    return true
  }

  override fun hashCode(): Int {
    var result = permissions.hashCode()
    result = 31 * result + grantResults.contentHashCode()
    return result
  }
}

internal operator fun AssentResult?.plus(other: AssentResult): AssentResult {
  if (this == null) {
    return other
  }
  return AssentResult(
      permissions = this.permissions + other.permissions,
      grantResults = this.grantResults + other.grantResults
  )
}
