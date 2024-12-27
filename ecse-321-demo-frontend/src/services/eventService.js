import api from './api'

export const eventService = {
  async getAllEvents() {
    try {
      const response = await api.get('/events')
      return response.data
    } catch (error) {
      console.error('Error fetching events:', error)
      throw error
    }
  },

  async getEventsByCreator(userId) {
    try {
      const response = await api.get(`/events?createdBy=${userId}`)
      return response.data
    } catch (error) {
      console.error('Error fetching user events:', error)
      throw error
    }
  },

  async updateEvent(eventId, eventData) {
    try {
      const response = await api.put(`/events/${eventId}`, eventData)
      return response.data
    } catch (error) {
      console.error('Error updating event:', error)
      throw error
    }
  },

  async getEventRegistrations(eventId) {
    try {
      const response = await api.get(`/registrations/${eventId}`)
      return response.data
    } catch (error) {
      console.error('Error fetching event registrations:', error)
      throw error
    }
  },

  async createEvent(eventData) {
    try {
      const response = await api.post('/events', eventData)
      return response.data
    } catch (error) {
      console.error('Error creating event:', error)
      throw error
    }
  },
}
