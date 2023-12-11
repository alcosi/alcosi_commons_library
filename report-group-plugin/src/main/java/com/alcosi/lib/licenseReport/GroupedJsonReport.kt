/*
 * Copyright (c) 2023  Alcosi Group Ltd. and affiliates.
 *
 * Portions of this software are licensed as follows:
 *
 *     All content that resides under the "alcosi" and "atomicon" or “deploy” directories of this repository, if that directory exists, is licensed under the license defined in "LICENSE.TXT".
 *
 *     All third-party components incorporated into this software are licensed under the original license provided by the owner of the applicable component.
 *
 *     Content outside of the above-mentioned directories or restrictions above is available under the MIT license as defined below.
 *
 *
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is urnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.alcosi.lib.licenseReport

import com.github.jk1.license.ModuleData
import com.github.jk1.license.render.JsonReportRenderer
import com.github.jk1.license.render.LicenseDataCollector
import java.util.*

class GroupedJsonReport(
    fileName: String = "group-report.json",
    onlyOneLicensePerModule: Boolean = true,
    val joinCommonLicenses: Boolean = false,
) : JsonReportRenderer(fileName, onlyOneLicensePerModule) {
    var isApache2 = { licenseNamePair: Pair<String, String?> ->
        licenseNamePair.first.lowercase().contains("Apache".lowercase()) &&
            licenseNamePair.first.lowercase()
                .contains("2".lowercase())
    }
    var isMIT =
        { licenseNamePair: Pair<String, String?> -> licenseNamePair.first.lowercase().contains("MIT".lowercase()) }
    var isEPL = { licenseNamePair: Pair<String, String?> ->
        licenseNamePair.first.lowercase().contains("Eclipse".lowercase()) ||
            licenseNamePair.first.lowercase()
                .contains("EPL".lowercase())
    }
    var isBSD3 =
        { licenseNamePair: Pair<String, String?> -> licenseNamePair.first.lowercase().contains("BSD-3".lowercase()) }
    var convertApache =
        { licenseNamePair: Pair<String, String?> -> if (isApache2(licenseNamePair)) "Apache License, Version 2.0" to "https://www.apache.org/licenses/LICENSE-2.0.txt" else licenseNamePair }
    var convertMIT =
        { licenseNamePair: Pair<String, String?> -> if (isMIT(licenseNamePair)) "The MIT License" to "http://opensource.org/licenses/MIT" else licenseNamePair }
    var convertEPL =
        { licenseNamePair: Pair<String, String?> -> if (isEPL(licenseNamePair)) "EPL 2.0" to "https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.txt" else licenseNamePair }
    var convertBSD3 =
        { licenseNamePair: Pair<String, String?> -> if (isBSD3(licenseNamePair)) "BSD-3-Clause" to "https://asm.ow2.io/license.html" else licenseNamePair }
    var commonPreformatters = listOf(convertApache, convertMIT, convertEPL, convertBSD3)

    override fun renderSingleLicensePerModule(allDependencies: MutableCollection<ModuleData>): Any {
        val infoMaps =
            allDependencies.map {
                val moduleName = "${it.group}:${it.name}"
                val moduleVersion = it.version
                val info = LicenseDataCollector.multiModuleLicenseInfo(it)
                val moduleUrl = info.moduleUrls.lastOrNull()
                val license = info.licenses.lastOrNull()
                val moduleLicense = license?.name
                val moduleLicenseUrl = license?.url
                val map =
                    listOf(
                        "moduleName" to moduleName,
                        "moduleUrl" to moduleUrl,
                        "moduleVersion" to moduleVersion,
                        "moduleLicenseName" to moduleLicense?.trim(),
                        "moduleLicenseUrl" to moduleLicenseUrl?.trim(),
                    ).filter { p -> p.second != null }
                        .associate { p -> p.first.trim() to p.second!!.trim() }
                return@map map
            }.filter { it["moduleLicenseName"] != null }
                .sortedBy { it["moduleName"] }
        return getGrouped(infoMaps)
    }

    protected fun getGrouped(mapListArg: List<Map<String, Any?>>): SortedMap<String, Any?> {
        var mapList: List<Map<String, Any?>> =
            if (joinCommonLicenses) {
                val mappedMap =
                    mapListArg.map { argMap ->
                        val mutable = argMap.toMutableMap()
                        val name = argMap["moduleLicenseName"] as String?
                        val uri = argMap["moduleLicenseUrl"] as String?
                        if (name == null) {
                            return@map argMap
                        }
                        val licenseNamePair: Pair<String, String?> = name to uri
                        val processedLicenseNamePair = commonPreformatters.fold(licenseNamePair) { acc, arg -> arg(acc) }
                        mutable["moduleLicenseName"] = processedLicenseNamePair.first
                        mutable["moduleLicenseUrl"] = processedLicenseNamePair.second
                        return@map mutable
                    }
                mappedMap
            } else {
                mapListArg
            }
        val licences =
            mapList
                .filter { it["moduleLicenseName"] != null }
                .map { it["moduleLicenseName"].toString().lowercase() }
                .distinct()
        val map: MutableMap<String, Any?> = mutableMapOf()
        licences.forEach { license ->
            val list = mapList.filter { license.equals(it["moduleLicenseName"]?.toString(), true) }
            val url = list.map { it["moduleLicenseUrl"]?.toString() }.firstOrNull()
            val licenseName = list.map { it["moduleLicenseName"]?.toString() }.firstOrNull()
            val licenseTitle = getLicence(licenseName, url) ?: return@forEach
            map[licenseTitle] =
                list.map { listLicense ->
                    listLicense.filter { it.key != "moduleLicenseName" }
                        .filter { it.key != "moduleLicenseUrl" }
                }
        }
        return map.toSortedMap(compareBy<String> { it })
    }

    override fun renderAllLicensesPerModule(allDependencies: MutableCollection<ModuleData>): Any {
        val infoMaps =
            allDependencies.flatMap {
                val moduleName = "${it.group}:${it.name}"
                val moduleVersion = it.version
                val info = LicenseDataCollector.multiModuleLicenseInfo(it)
                val licenses = info.licenses
                val map =
                    licenses.map { l ->
                        listOf(
                            "moduleName" to moduleName?.trim(),
                            "moduleUrls" to info.moduleUrls?.map { m -> m.trim() },
                            "moduleVersion" to moduleVersion?.trim(),
                            "moduleLicenseName" to l.name?.trim(),
                            "moduleLicenseUrl" to l.url?.trim(),
                        ).filter { p -> p.second != null }.toMap()
                    }
                return@flatMap map
            }
                .filter { it["moduleLicenseName"] != null }
                .sortedBy { it["moduleName"] as String? }
        return getGrouped(infoMaps)
    }

    private fun getLicence(
        moduleLicense: String?,
        moduleLicenseUrl: String?,
    ) = if (moduleLicense == null) {
        null
    } else {
        "${moduleLicense.trim()} (${moduleLicenseUrl?.trim()})".trim()
    }
}
