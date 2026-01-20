package com.zhangke.fread.profile.screen.donate

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.common.browser.launchWebTabInApp
import com.zhangke.fread.common.config.AppCommonConfig
import com.zhangke.fread.feature.profile.Res
import com.zhangke.fread.feature.profile.af_dian
import com.zhangke.fread.feature.profile.kofi_symbol
import com.zhangke.fread.localization.LocalizedString
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Serializable
object DonateScreenNavKey : NavKey

@Composable
fun DonateScreen() {
    val browserLauncher = LocalActivityBrowserLauncher.current
    val coroutineScope = rememberCoroutineScope()
    DonateContent(
        onDonateClick = {
            browserLauncher.launchWebTabInApp(
                scope = coroutineScope,
                url = it.url,
                checkAppSupportPage = false,
            )
        },
    )
}

@Composable
private fun DonateContent(
    onDonateClick: (DonateItem) -> Unit,
) {
    Surface(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            Text(
                text = stringResource(LocalizedString.profileDonatePageTitle),
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(16.dp))
            val donateList = remember { buildDonateList() }
            for (donateItem in donateList) {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onDonateClick(donateItem) },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        modifier = Modifier.size(24.dp)
                            .clip(CircleShape),
                        painter = donateItem.logo(),
                        contentDescription = null,
                    )
                    Column(
                        modifier = Modifier.weight(1F).padding(start = 16.dp),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Text(
                            text = donateItem.title,
                            style = MaterialTheme.typography.bodyMedium
                                .copy(fontWeight = FontWeight.SemiBold),
                        )
                        Text(
                            modifier = Modifier,
                            text = donateItem.url,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

private fun buildDonateList(): List<DonateItem> {
    return listOf(
        DonateItem.kofi(),
        DonateItem.afDian(),
    )
}

data class DonateItem(
    val title: String,
    val url: String,
    val logo: @Composable () -> Painter,
) {

    companion object {

        fun kofi(): DonateItem {
            return DonateItem(
                title = "Ko-fi",
                url = AppCommonConfig.DONATE_KO_FI_LINK,
                logo = {
                    painterResource(Res.drawable.kofi_symbol)
                },
            )
        }

        fun afDian(): DonateItem {
            return DonateItem(
                title = "AFDIAN(爱发电)",
                url = AppCommonConfig.DONATE_AF_DIAN_LINK,
                logo = {
                    painterResource(Res.drawable.af_dian)
                },
            )
        }
    }
}
