<script lang="ts">
    import { authStore } from '$lib/stores/auth';

    export let isOpen = false;
    let username = '';
    let password = '';
    let isRegistering = false;

    async function handleSubmit() {
        const endpoint = isRegistering ? '/api/users' : '/api/users/login';
        try {
            const response = await fetch(endpoint, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ username, password }),
            });

            if (response.ok) {
                const data = await response.json();
                authStore.update(state => ({
                    ...state,
                    isAuthenticated: true,
                    userId: data.userId,
                    username
                }));
                isOpen = false;
            }
        } catch (error) {
            console.error('Authentication failed:', error);
        }
    }
</script>

{#if isOpen}
    <div class="modal modal-open">
        <div class="modal-box">
            <h3 class="font-bold text-lg">
                {isRegistering ? 'Register' : 'Login'}
            </h3>

            <form on:submit|preventDefault={handleSubmit} class="form-control w-full">
                <label class="label">
                    <span class="label-text">Username</span>
                </label>
                <input
                    type="text"
                    bind:value={username}
                    class="input input-bordered w-full"
                />

                <label class="label">
                    <span class="label-text">Password</span>
                </label>
                <input
                    type="password"
                    bind:value={password}
                    class="input input-bordered w-full"
                />

                <div class="modal-action">
                    <button type="submit" class="btn btn-primary">
                        {isRegistering ? 'Register' : 'Login'}
                    </button>

                    <button
                        type="button"
                        class="btn"
                        on:click={() => isRegistering = !isRegistering}
                    >
                        {isRegistering
                            ? 'Already have an account?'
                            : 'Need to register?'}
                    </button>

                    <button
                        type="button"
                        class="btn"
                        on:click={() => isOpen = false}
                    >
                        Close
                    </button>
                </div>
            </form>
        </div>
    </div>
{/if}
