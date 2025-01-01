-- This is some squeal to create a bunch of dummy data (Thanks Claude!!!)

-- Insert specific user accounts
INSERT INTO user_account (id, username, password, created_at)
VALUES
    ('11111111-1111-1111-1111-111111111111'::uuid, 'jessie.galasso', 'password123', CURRENT_TIMESTAMP),
    ('22222222-2222-2222-2222-222222222222'::uuid, 'ray.liu', 'password123', CURRENT_TIMESTAMP),
    ('33333333-3333-3333-3333-333333333333'::uuid, 'alex.liu', 'password123', CURRENT_TIMESTAMP),
    ('44444444-4444-4444-4444-444444444444'::uuid, 'jean.paul', 'password123', CURRENT_TIMESTAMP);

-- Generate 96 random users
INSERT INTO user_account (id, username, password, created_at)
SELECT
    md5(random()::text)::uuid,
    'user_' || generate_series || '_' || md5(random()::text)::varchar(6),
    'password123',
    CURRENT_TIMESTAMP + (random() * interval '30 days')
FROM generate_series(1, 96);

-- Generate 25 online events for each specific user (total 100)
INSERT INTO events (
    id,
    created_by_id,
    created_at,
    description,
    start_time,
    end_time,
    participants_count,
    capacity,
    event_type,
    invite_link
)
SELECT
    md5(random()::text)::uuid,
    CASE
        WHEN generate_series <= 25 THEN '11111111-1111-1111-1111-111111111111'::uuid
        WHEN generate_series <= 50 THEN '22222222-2222-2222-2222-222222222222'::uuid
        WHEN generate_series <= 75 THEN '33333333-3333-3333-3333-333333333333'::uuid
        ELSE '44444444-4444-4444-4444-444444444444'::uuid
    END,
    CURRENT_TIMESTAMP + (random() * interval '10 days'),
    'Online Event ' || generate_series || ': ' ||
    (CASE (random() * 4)::int
        WHEN 0 THEN 'JavaScript Workshop'
        WHEN 1 THEN 'Python Masterclass'
        WHEN 2 THEN 'Data Science Seminar'
        WHEN 3 THEN 'Web Development Course'
        ELSE 'Machine Learning Workshop'
    END),
    CURRENT_TIMESTAMP + (random() * interval '30 days'),
    CURRENT_TIMESTAMP + (random() * interval '30 days') + interval '2 hours',
    0,
    (random() * 50 + 20)::int,
    'ONLINE',
    'https://meet.virtual.com/' || md5(random()::text)
FROM generate_series(1, 100);

-- Generate 25 in-person events for each specific user (total 100)
INSERT INTO events (
    id,
    created_by_id,
    created_at,
    description,
    start_time,
    end_time,
    participants_count,
    capacity,
    event_type,
    location
)
SELECT
    md5(random()::text)::uuid,
    CASE
        WHEN generate_series <= 25 THEN '11111111-1111-1111-1111-111111111111'::uuid
        WHEN generate_series <= 50 THEN '22222222-2222-2222-2222-222222222222'::uuid
        WHEN generate_series <= 75 THEN '33333333-3333-3333-3333-333333333333'::uuid
        ELSE '44444444-4444-4444-4444-444444444444'::uuid
    END,
    CURRENT_TIMESTAMP + (random() * interval '10 days'),
    'In-Person Event ' || generate_series || ': ' ||
    (CASE (random() * 4)::int
        WHEN 0 THEN 'Coding Bootcamp'
        WHEN 1 THEN 'Tech Conference'
        WHEN 2 THEN 'Hackathon'
        WHEN 3 THEN 'Networking Event'
        ELSE 'Workshop'
    END),
    CURRENT_TIMESTAMP + (random() * interval '30 days'),
    CURRENT_TIMESTAMP + (random() * interval '30 days') + interval '4 hours',
    0,
    (random() * 30 + 15)::int,
    'INPERSON',
    (CASE (random() * 4)::int
        WHEN 0 THEN 'Tech Hub, ' || (random() * 1000)::int || ' Main Street'
        WHEN 1 THEN 'Innovation Center, ' || (random() * 1000)::int || ' Tech Avenue'
        WHEN 2 THEN 'University Campus, Building ' || (random() * 10)::int
        WHEN 3 THEN 'Conference Center, Room ' || (random() * 100)::int
        ELSE 'Startup Space, ' || (random() * 500)::int || ' Innovation Drive'
    END)
FROM generate_series(1, 100);

WITH RECURSIVE RandomRegistrations AS (
    -- Get all possible user-event combinations
    SELECT
        u.id AS user_id,
        e.id AS event_id,
        ROW_NUMBER() OVER (PARTITION BY u.id ORDER BY random()) AS reg_number
    FROM user_account u
    CROSS JOIN events e
    WHERE e.capacity > e.participants_count  -- Only consider events with space
),
FilteredRegistrations AS (
    SELECT
        rr.user_id,
        rr.event_id
    FROM RandomRegistrations rr
    JOIN events e ON e.id = rr.event_id
    WHERE rr.reg_number <= 25  -- Take 25 registrations per user
    AND e.created_by_id != rr.user_id  -- Users can't register for their own events
)
INSERT INTO registration (event_id, user_id, registered_at)
SELECT DISTINCT event_id, user_id, CURRENT_TIMESTAMP + (random() * interval '30 days')
FROM FilteredRegistrations
WHERE (SELECT COUNT(*) FROM registration r WHERE r.event_id = FilteredRegistrations.event_id) < (SELECT capacity FROM events e WHERE e.id = FilteredRegistrations.event_id);

-- Update participant counts
UPDATE events e
SET participants_count = (
    SELECT COUNT(*)
    FROM registration r
    WHERE r.event_id = e.id
);

-- Ensure event end times are after start times
UPDATE events
SET end_time = start_time + interval '2 hours'
WHERE end_time <= start_time;
