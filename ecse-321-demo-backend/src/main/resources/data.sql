-- This is some squeal to create a bunch of dummy data (Thanks Claude!!!)

-- Insert specific user accounts
INSERT INTO user_account (id, username, password, created_at)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'jessie.galasso', 'password123', CURRENT_TIMESTAMP),
    ('22222222-2222-2222-2222-222222222222', 'ray.liu', 'password123', CURRENT_TIMESTAMP),
    ('33333333-3333-3333-3333-333333333333', 'alex.liu', 'password123', CURRENT_TIMESTAMP),
    ('44444444-4444-4444-4444-444444444444', 'jean.paul', 'password123', CURRENT_TIMESTAMP);

-- Generate 96 random users
INSERT INTO user_account (id, username, password, created_at)
SELECT
    md5(random()::text)::uuid,
    'user_' || generate_series || '_' || md5(random()::text)::varchar(6),
    'password123',
    CURRENT_TIMESTAMP + (random() * interval '30 days')
FROM generate_series(1, 96);

-- Generate 50 online events
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
    (SELECT id FROM user_account ORDER BY random() LIMIT 1),
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
FROM generate_series(1, 50);

-- Generate 50 in-person events
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
    (SELECT id FROM user_account ORDER BY random() LIMIT 1),
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
FROM generate_series(1, 50);

-- Register each user for 2 online and 2 in-person events
WITH UserEvents AS (
    SELECT
        u.id as user_id,
        array_agg(e.id) as event_ids
    FROM user_account u
    CROSS JOIN LATERAL (
        SELECT id
        FROM events
        WHERE event_type = 'ONLINE'
        ORDER BY random()
        LIMIT 2
    ) e
    GROUP BY u.id
    UNION ALL
    SELECT
        u.id as user_id,
        array_agg(e.id) as event_ids
    FROM user_account u
    CROSS JOIN LATERAL (
        SELECT id
        FROM events
        WHERE event_type = 'INPERSON'
        ORDER BY random()
        LIMIT 2
    ) e
    GROUP BY u.id
)
INSERT INTO registration (event_id, user_id)
SELECT
    unnest(event_ids),
    user_id
FROM UserEvents;

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
