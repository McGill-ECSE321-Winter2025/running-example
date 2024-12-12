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
}
