TRUNCATE TABLE
    access_tokens,
    availabilities,
    group_users,
    groups,
    notifications,
    refresh_tokens,
    schedules,
    shifts,
    user_delete_requests,
    users
    RESTART IDENTITY CASCADE;
