package com.codingwithrufat.userapplication.network.models

import com.google.gson.annotations.SerializedName

data class UserResponse(
	val quotaMax: Int? = null,
	val quotaRemaining: Int? = null,
	val hasMore: Boolean? = null,
	val items: List<ItemsItem?>? = null
)

data class Collective(
	val externalLinks: List<ExternalLinksItem?>? = null,
	val link: String? = null,
	val name: String? = null,
	val description: String? = null,
	val slug: String? = null,
	val tags: List<String?>? = null
)

data class CollectivesItem(
	val role: String? = null,
	val collective: Collective? = null
)

data class ExternalLinksItem(
	val link: String? = null,
	val type: String? = null
)

data class ItemsItem(
	val reputationChangeQuarter: Int? = null,
	val link: String? = null,
	val lastModifiedDate: Int? = null,
	val lastAccessDate: Int? = null,
	val reputation: Int? = null,
	val badgeCounts: BadgeCounts? = null,

	@SerializedName("creation_date")
	val creationDate: Int? = null,

	@SerializedName("display_name")
	val displayName: String? = null,

	val reputationChangeYear: Int? = null,
	val acceptRate: Int? = null,
	val isEmployee: Boolean? = null,

	@SerializedName("profile_image")
	val profileImage: String? = null,

	val accountId: Int? = null,
	val userType: String? = null,
	val websiteUrl: String? = null,
	val reputationChangeWeek: Int? = null,

	@SerializedName("user_id")
	val userId: Int? = null,

	val reputationChangeDay: Int? = null,
	val location: String? = null,
	val reputationChangeMonth: Int? = null,
	val collectives: List<CollectivesItem?>? = null
)

data class BadgeCounts(
	val gold: Int? = null,
	val silver: Int? = null,
	val bronze: Int? = null
)

