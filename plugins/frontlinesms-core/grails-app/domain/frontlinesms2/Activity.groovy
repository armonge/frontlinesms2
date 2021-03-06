package frontlinesms2

abstract class Activity extends MessageOwner {
//> STATIC PROPERTIES
	static boolean editable = { true }
	static def implementations = [Announcement, Autoreply, Autoforward, Poll, Subscription, Webconnection]
	protected static final def NAME_VALIDATOR = { activityDomainClass ->
		return { val, obj ->
			if(obj?.deleted || obj?.archived) return true
			def identical = activityDomainClass.findAllByNameIlike(val)
			if(!identical) return true
			else if (identical.any { it.id != obj.id && !it?.archived && !it?.deleted }) return false
			else return true
		}
	}

//> INSTANCE PROPERTIES
	String sentMessageText
	Date dateCreated
	static transients = ['liveMessageCount']

	static hasMany = [keywords: Keyword]
	List keywords

	static mapping = {
		tablePerHierarchy false
		version false
		keywords cascade: "all-delete-orphan"
	}

	static constraints = {
		sentMessageText(nullable:true)
		keywords(validator: { val, obj ->
			!val || val?.every { it.validate() }
		})
	}

//> ACCESSORS
	def getActivityMessages(getOnlyStarred=false, getSent=null) {
		Fmessage.owned(this, getOnlyStarred, getSent)
	}
	
	def getLiveMessageCount() {
		def m = Fmessage.findAllByMessageOwnerAndIsDeleted(this, false)
		m ? m.size() : 0
	}

//> ACTIONS
	def archive() {
		this.archived = true
		messages.each {
			it.archived = true
		}
	}
	
	def unarchive() {
		this.archived = false
		messages.each { it.archived = false }
	}

	def restoreFromTrash() {
		this.deleted = false
		this.messages*.isDeleted = false
	}

	def processKeyword(Fmessage message, Keyword match) {}

	/**
	 * Activcate this activity.  If it is already activated, this method should
	 * deactivate it and then reactivate it.
	 */
	def activate() {}

	def deactivate() {}

	private def logFail(c, ex) {
		ex.printStackTrace()
		log.warn("Error creating routes of webconnection with id $c?.id", ex)
		LogEntry.log("Error creating routes to webconnection with name ${c?.name?: c?.id}")
		//createSystemNotification('connection.route.failNotification', [c.id, c?.name?:c?.id], ex)
	}
}

