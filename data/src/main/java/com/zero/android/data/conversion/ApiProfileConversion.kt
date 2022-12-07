package com.zero.android.data.conversion

import com.zero.android.database.model.ProfileEntity
import com.zero.android.models.Profile
import com.zero.android.network.model.ApiProfile

internal fun ApiProfile.toModel() =
	Profile(
		id = id,
		firstName = firstName,
		lastName = lastName,
		profileImage = profileImage,
		handle = profileHandle?.handle,
		type = profileHandle?.profileType,
		gender = gender,
		guild = guild,
		guildId = guildId,
		summary = summary,
		skills = skills?.map { it.toModel() },
		values = values?.map { it.toModel() },
		passions = passions?.map { it.toModel() },
		languages = languages?.map { it.toModel() },
		primaryCity = primaryCity?.toModel(),
		secondaryCity = secondaryCity?.toModel(),
		hometownCity = hometownCity?.toModel(),
		createdAt = createdAt,
		primaryEmail = primaryEmail,
		secondaryEmail = secondaryEmail,
		primaryPhone = primaryPhone,
		secondaryPhone = secondaryPhone,
		website = website,

		// Social media
		facebook = facebook,
		twitter = twitter,
		instagram = instagram,
		linkedIn = linkedIn,
		medium = medium,
		github = github,
		foursquare = foursquare,
		pinterest = pinterest,
		behance = behance,
		dribble = dribble,
		youtube = youtube,
		myspace = myspace,
		tumblr = tumblr,
		flickr = flickr,
		wikipedia = wikipedia,
		soundcloud = soundcloud,
		spotify = spotify,
		appleMusic = appleMusic,

		// Communication
		wechat = wechat,
		signal = signal,
		snapchat = snapchat,
		skype = skype,
		zoom = zoom,
		slack = slack,
		telegram = telegram,
		whatsapp = whatsapp,

		// Extra info
		experiences = experiences?.map { it.toModel() },
		investments = investments?.map { it.toModel() },
		educationRecords = educationRecords?.map { it.toModel() },
		rawAvatarURL = rawAvatarURL,
		_wallpaperURL = _wallpaperURL
	)

internal fun ApiProfile.toEntity(userId: String) =
	ProfileEntity(
		id = id,
		userId = userId,
		firstName = firstName,
		lastName = lastName,
		profileImage = profileImage,
		gender = gender,
		type = profileHandle?.profileType,
		handle = profileHandle?.handle,
		guildId = guildId,
		guild = guild,
		summary = summary,
		skills = skills?.map { it.toEntity() },
		values = values?.map { it.toEntity() },
		passions = passions?.map { it.toEntity() },
		languages = languages?.map { it.toEntity() },
		primaryCity = primaryCity?.toEntity(),
		secondaryCity = secondaryCity?.toEntity(),
		hometownCity = hometownCity?.toEntity(),
		createdAt = createdAt,
		primaryEmail = primaryEmail,
		secondaryEmail = secondaryEmail,
		primaryPhone = primaryPhone,
		secondaryPhone = secondaryPhone,
		website = website,

		// Social media
		facebook = facebook,
		twitter = twitter,
		instagram = instagram,
		linkedIn = linkedIn,
		medium = medium,
		github = github,
		foursquare = foursquare,
		pinterest = pinterest,
		behance = behance,
		dribble = dribble,
		youtube = youtube,
		myspace = myspace,
		tumblr = tumblr,
		flickr = flickr,
		wikipedia = wikipedia,
		soundcloud = soundcloud,
		spotify = spotify,
		appleMusic = appleMusic,

		// Communication
		wechat = wechat,
		signal = signal,
		snapchat = snapchat,
		skype = skype,
		zoom = zoom,
		slack = slack,
		telegram = telegram,
		whatsapp = whatsapp,

		// Extra info
		experiences = experiences?.map { it.toEntity() },
		investments = investments?.map { it.toEntity() },
		educationRecords = educationRecords?.map { it.toEntity() },
		rawAvatarURL = rawAvatarURL,
		_wallpaperURL = _wallpaperURL
	)
