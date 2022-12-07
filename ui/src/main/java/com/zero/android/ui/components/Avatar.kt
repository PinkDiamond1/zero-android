package com.zero.android.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zero.android.common.R
import com.zero.android.models.Member

@Composable
fun Avatar(modifier: Modifier = Modifier, user: Member?, size: Dp = 36.dp) =
	CircularImage(
		modifier = modifier,
		url = user?.image,
		size = size,
		placeholder = painterResource(R.drawable.ic_user_profile_placeholder),
		contentDescription = user?.name ?: ""
	)
