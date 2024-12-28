import api from './api'

export const eventService = {
  // GET /events
  async getAllEvents() {
    try {
      const response = await api.get('/events')
      return response.data
    } catch (error) {
      console.error('Error fetching events:', error)
      throw error
    }
  },

  // GET /events?createdBy={userId}
  async getEventsByCreator(userId) {
    try {
      const response = await api.get(`/events?createdBy=${userId}`)
      return response.data
    } catch (error) {
      console.error('Error fetching user events:', error)
      throw error
    }
  },

  // GET /events/{eventId}
  async getEvent(eventId) {
    try {
      const response = await api.get(`/events/${eventId}`)
      return response.data
    } catch (error) {
      console.error('Error fetching event:', error)
      throw error
    }
  },

  // PUT /events/{eventId}
  async updateEvent(eventId, eventData) {
    try {
      const response = await api.put(`/events/${eventId}`, eventData)
      return response.data
    } catch (error) {
      console.error('Error updating event:', error)
      throw error
    }
  },

  // POST /events
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
