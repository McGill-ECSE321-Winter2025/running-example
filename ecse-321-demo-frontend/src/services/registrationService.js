import api from './api'

export const registrationService = {
  async getRegistrationsByEvent(eventId) {
    try {
      const response = await api.get(`/registrations/${eventId}`)
      return response.data
    } catch (error) {
      console.error('Error fetching event registrations:', error)
      throw error
    }
  },
}
