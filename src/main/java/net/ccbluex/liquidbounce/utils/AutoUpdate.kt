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
    val CURRENT_VERSION = File("version.txt").readText().trim()
    // Gọi hàm này lúc start game hoặc ở nơi phù hợp
    fun checkAndUpdate() {
        try {
            val json = URL(API_URL).readText()
            val release = JSONObject(json)
            val latestTag = release.getString("tag_name")
            if (latestTag == CURRENT_VERSION) return // Đúng version rồi

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

            // Thông báo cho user
            val res = JOptionPane.showConfirmDialog(null, "Có bản cập nhật mới ($latestTag)! Bạn có muốn tải và restart?", "Auto Update", JOptionPane.YES_NO_OPTION)
            if (res == JOptionPane.YES_OPTION) {
                val newJar = File("LiquidBounce_Update.jar")
                URL(downloadUrl).openStream().use { input ->
                    newJar.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                // Tự restart (chạy file jar mới)
                Runtime.getRuntime().exec("java -jar ${newJar.absolutePath}")
                // Thoát game hiện tại
                System.exit(0)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}