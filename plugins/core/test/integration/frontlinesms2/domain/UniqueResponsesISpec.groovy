package frontlinesms2.domain

import frontlinesms2.*

class UniqueResponsesISpec extends grails.plugin.spock.IntegrationSpec {
	def 'Poll must have unique responses'() {
		when:
			def p = new Poll(title:'Poll Fail', responses:[new PollResponse(value:'same'),
						new PollResponse(value:'tother'), new PollResponse(value:'same'), new PollResponse(value:'last')])
		then:
			!p.validate()
	}

	def 'Responses can be the same only if they are assigned to different polls'() {
		given:
			createTestData()
		when:
			def r = PollResponse.findAllByValue('one')
		then:
			r.size() == 2
	}

	static createTestData() {
		new Poll(name:'Test', responses:[new PollResponse(value:'one'),
						new PollResponse(value:'other'), new PollResponse(value:'Unknown')]).save(flush:true, failOnError:true)

		new Poll(name:'Second', responses:[new PollResponse(value:'one'),
						new PollResponse(value:'two'), new PollResponse(value:'three'), new PollResponse(value:'Unknown')]).save(flush:true, failOnError:true)
	}

	static deleteTestData() {
		Poll.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}
	}
}
