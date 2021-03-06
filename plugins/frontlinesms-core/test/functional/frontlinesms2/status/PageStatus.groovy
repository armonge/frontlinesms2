package frontlinesms2.status

import frontlinesms2.*
import frontlinesms2.page.*

class PageStatus extends PageBase {
	static getUrl() { 'status' }
	static at = {
		title.startsWith('Status')
	}
	static content = {
		statusButton { $('#update-chart') }
		detectModems { $('#device-detection a') }
		detectedDevicesSection { $('div#device-detection') }
		noConnections { $("div#connection-status p.no-content") }
		connectionByName { connName -> $("#connection-${SmslibFconnection.findByName(connName).id}") }
		noDevicesDetectedNotification(required:false) { detectedDevicesSection.find('td.no-content') }
		detectedDevicesTable(required:false) { detectedDevicesSection.find('table') }
		detectedDevicesRows { detectedDevicesTable.find('tbody tr') }
		trafficForm { $("#trafficForm")}
		typeFilters(required:false) { $("#type-filters")}
		activityFilter(required:false) { typeFilters.find("#activityId")}
		detectionTitle { $("#detection-title").text() }
	}
}
