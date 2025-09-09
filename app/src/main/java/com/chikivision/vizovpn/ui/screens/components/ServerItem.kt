package com.chikivision.vizovpn.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape // ✅ THE FIX: This import was missing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chikivision.vizovpn.ui.theme.DisconnectedBlue
import com.chikivision.vizovpn.ui.theme.PremiumYellow
import com.chikivision.vizovpn.ui.theme.TextPrimary
import com.chikivision.vizovpn.ui.theme.TextSecondary

@Composable
fun ServerItem(
    name: String,
    speed: String,
    isSelected: Boolean,
    isPremium: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) DisconnectedBlue.copy(alpha = 0.1f) else Color.Transparent
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)) // This line was causing the error
            .background(backgroundColor)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // TODO: Add flag emoji based on countryCode
            Text(name, fontWeight = FontWeight.Medium, color = TextPrimary)
            if (isPremium) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Premium",
                    tint = PremiumYellow,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(start = 4.dp)
                )
            }
        }
        Text(speed, color = TextSecondary, fontSize = 12.sp)
    }
}