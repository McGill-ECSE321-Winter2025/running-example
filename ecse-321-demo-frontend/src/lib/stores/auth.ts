import { writable } from 'svelte/store';

interface AuthState {
  isAuthed: boolean,
  userId: string | null,
  username: string | null
}

export const authStore = writable<AuthState>({
  isAuthed: false,
  userId: null,
  username: null
});
