package com.zero.android.feature.messages.util

import com.zero.android.common.util.DateUtil
import com.zero.android.models.DraftMessage
import com.zero.android.models.Member
import com.zero.android.models.enums.MessageMentionType
import com.zero.android.models.enums.MessageType
import java.io.File

object MessageUtil {

	fun newTextMessage(msg: String, authorId: String, channelMembers: List<Member>) =
		DraftMessage(
			channelId = "",
			author = Member(authorId),
			type = MessageType.TEXT,
			mentionType = MessageMentionType.USER,
			message = prepareMessage(msg, channelMembers).trim(),
			createdAt = DateUtil.currentTimeMillis(),
			updatedAt = DateUtil.currentTimeMillis(),
			mentions = getMentionedUsers(msg, channelMembers).map { it.id }
		)

	private fun getMessageMentions(msg: String): Sequence<String> {
		val regex = Regex("(@\\w+)")
		return regex.findAll(msg).map { it.value }
	}

	fun prepareMessage(msg: String, channelMembers: List<Member>): String {
		var updatedMessage = msg
		val matches = getMessageMentions(msg)
		matches.distinct().forEach { mention ->
			val mentionedUser: String = mention.replace("_", " ").trim().drop(1)
			val member = channelMembers.firstOrNull { mentionedUser.equals(it.name?.trim(), true) }
			member?.let {
				val updatedMention = "@[${mention.drop(1)}]"
				updatedMessage = updatedMessage.replace(mention, "$updatedMention(user:${it.id})")
			}
		}
		return updatedMessage
	}

	fun getMentionedUsers(msg: String, channelMembers: List<Member>): List<Member> {
		val matches = getMessageMentions(msg)
		val messageMentions = matches.map { it.replace("_", " ").trim().drop(1) }.toList()
		val members =
			channelMembers.filter { member ->
				messageMentions.any { it.equals(member.name?.trim(), true) }
			}
		return members
	}

	fun newFileMessage(file: File, authorId: String, type: MessageType) =
		DraftMessage(
			channelId = "",
			author = Member(authorId),
			type = type,
			mentionType = MessageMentionType.USER,
			file = file,
			fileName = file.name,
			fileMimeType = type.serializedName,
			createdAt = DateUtil.currentTimeMillis(),
			updatedAt = DateUtil.currentTimeMillis()
		)
}
