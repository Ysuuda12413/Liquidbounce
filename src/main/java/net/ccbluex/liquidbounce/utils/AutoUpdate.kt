package net.ccbluex.liquidbounce.utils

import org.json.JSONObject
import java.io.File
import java.net.URL
import javax.swing.JOptionPane

object AutoUpdate {
    private const val API_URL = "https://api.github.com/repos/Ysuuda12413/Liquidbounce/releases/latest"
    private const val JAR_PREFIX = "liquidbounce-"
    private const val JAR_SUFFIX = ".jar"
    private const val MC_VERSION = "mc1.8.9"
    val CURRENT_VERSION = try {
        AutoUpdate::class.java.classLoader.getResourceAsStream("version.txt")?.bufferedReader()?.readText()?.trim() ?: "unknown"
    } catch (e: Exception) { "unknown" }

    fun checkAndUpdate() {
        try {
            val json = URL(API_URL).readText()
            val release = JSONObject(json)
            val latestTag = release.getString("tag_name")
            if (latestTag == CURRENT_VERSION) return

            val assets = release.getJSONArray("assets")
            var downloadUrl: String? = null

            for (i in 0 until assets.length()) {
                val asset = assets.getJSONObject(i)
                val name = asset.getString("name")
                if (name.startsWith(JAR_PREFIX) && name.contains(MC_VERSION) && name.endsWith(JAR_SUFFIX)) {
                    downloadUrl = asset.getString("browser_download_url")
                    break
                }
            }
            if (downloadUrl == null) return

            val res = JOptionPane.showConfirmDialog(
                null,
                "Có bản cập nhật mới ($latestTag)! Bạn có muốn tải và ghi đè lên bản hiện tại không? (Yêu cầu restart)",
                "Auto Update",
                JOptionPane.YES_NO_OPTION
            )
            if (res == JOptionPane.YES_OPTION) {
                // Lấy đường dẫn file jar hiện tại
                val jarPath = File(javaClass.protectionDomain.codeSource.location.toURI()).absolutePath
                val tmpFile = File.createTempFile("liquidbounce_update", ".jar")

                // Tải file mới về file tạm
                URL(downloadUrl).openStream().use { input ->
                    tmpFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                // Ghi đè file jar cũ bằng file mới
                tmpFile.copyTo(File(jarPath), overwrite = true)
                tmpFile.delete()

                JOptionPane.showMessageDialog(
                    null,
                    "Đã cập nhật thành công! Vui lòng khởi động lại LiquidBounce."
                )
                System.exit(0)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}