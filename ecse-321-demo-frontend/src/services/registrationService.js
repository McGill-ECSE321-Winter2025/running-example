import api from './api'

export const registrationService = {
  // POST /registrations/{eventId}
  async registerForEvent(eventId) {
    try {
      const response = await api.post(`/registrations/${eventId}`)
      return response.data
    } catch (error) {
      console.error('Error registering for event:', error)
      throw error
    }
  },

  // DELETE /registrations/{eventId}
  async unregisterFromEvent(eventId) {
    try {
      const response = await api.delete(`/registrations/${eventId}`)
      return response.data
    } catch (error) {
      console.error('Error unregistering from event:', error)
      throw error
    }
  },

  // GET /registrations/my-registrations
  async getUserRegistrations() {
    try {
      const response = await api.get('/registrations/my-registrations')
      return response.data
    } catch (error) {
      console.error('Error fetching user registrations:', error)
      throw error
    }
  },

  // GET /registrations/{eventId}
  async getEventRegistrations(eventId) {
    try {
      const response = await api.get(`/registrations/${eventId}`)
      return response.data
    } catch (error) {
      console.error('Error fetching event registrations:', error)
      throw error
    }
  },
}
