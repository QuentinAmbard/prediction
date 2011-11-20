package com.avricot.prediction;

import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;

public abstract class CandidatStreamListener implements UserStreamListener {

	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
		System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
	}

	public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
		System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
	}

	public void onScrubGeo(long userId, long upToStatusId) {
		System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
	}

	public void onException(Exception ex) {
		ex.printStackTrace();
	}

	@Override
	public void onDeletionNotice(long directMessageId, long userId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFriendList(long[] friendIds) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFavorite(User source, User target, Status favoritedStatus) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnfavorite(User source, User target, Status unfavoritedStatus) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFollow(User source, User followedUser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRetweet(User source, User target, Status retweetedStatus) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDirectMessage(DirectMessage directMessage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserListSubscription(User subscriber, User listOwner, UserList list) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserListCreation(User listOwner, UserList list) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserListUpdate(User listOwner, UserList list) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserListDeletion(User listOwner, UserList list) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserProfileUpdate(User updatedUser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBlock(User source, User blockedUser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnblock(User source, User unblockedUser) {
		// TODO Auto-generated method stub

	}

};