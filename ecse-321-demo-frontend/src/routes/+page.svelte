<script lang="ts">
    import { onMount } from 'svelte';
    import type { EventResponse } from '$lib/types';

    let events: EventResponse[] = [];

    onMount(async () => {
        try {
            const response = await fetch('/api/events');
            events = await response.json();
        } catch (error) {
            console.error('Failed to fetch events:', error);
        }
    });
</script>

<div class="p-4">
    <h1 class="text-2xl font-bold mb-4">Upcoming Events</h1>

    <div class="overflow-x-auto">
        <table class="table">
            <thead>
                <tr>
                    <th>Description</th>
                    <th>Type</th>
                    <th>Start Time</th>
                    <th>Location/Link</th>
                    <th>Capacity</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                {#each events as event}
                    <tr>
                        <td>{event.description}</td>
                        <td>{event.eventType}</td>
                        <td>{new Date(event.startTime.nanos).toLocaleString()}</td>
                        <td>{event.locationOrLink}</td>
                        <td>{event.capacity}</td>
                        <td>
                            <button class="btn btn-primary btn-sm">Register</button>
                        </td>
                    </tr>
                {/each}
            </tbody>
        </table>
    </div>
</div>
