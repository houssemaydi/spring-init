-- Insert permissions
INSERT INTO permissions (name, description)
VALUES ('USER_READ', 'Lire les détails utilisateurs'),
       ('USER_WRITE', 'Modifier des utilisateurs'),
       ('USER_DELETE', 'Supprimer des utilisateurs'),
       ('ROLE_READ', 'Lire les rôles'),
       ('ROLE_WRITE', 'Modifier des rôles'),
       ('ROLE_DELETE', 'Supprimer des rôles'),
       ('PERMISSION_READ', 'Lire les permissions'),
       ('PERMISSION_WRITE', 'Modifier des permissions'),
       ('PERMISSION_DELETE', 'Supprimer des permissions');

-- Insert roles
INSERT INTO roles (name, description)
VALUES ('ADMIN', 'Administrateur du système'),
       ('MANAGER', 'Gestionnaire de l''application'),
       ('USER', 'Utilisateur standard');

-- Insert role permissions for ADMIN
INSERT INTO role_permissions (role_id, permission_id)
SELECT (SELECT id FROM roles WHERE name = 'ADMIN'), id
FROM permissions;

-- Insert role permissions for MANAGER
INSERT INTO role_permissions (role_id, permission_id)
SELECT (SELECT id FROM roles WHERE name = 'MANAGER'), id
FROM permissions
WHERE name IN ('USER_READ', 'USER_WRITE', 'ROLE_READ', 'PERMISSION_READ');

-- Insert role permissions for USER
INSERT INTO role_permissions (role_id, permission_id)
SELECT (SELECT id FROM roles WHERE name = 'USER'), id
FROM permissions
WHERE name = 'USER_READ';

-- Insert default users (passwords are encoded versions of: admin123, manager123, user123)
INSERT INTO users (username, email, password, enabled, account_non_locked, credentials_non_expired, account_non_expired)
VALUES ('admin', 'admin@example.com', '$2a$10$ub1a6WYTZ/zMR12C.UV5KOyxEwCNyRgIYKxLg5QGCKnDCpjnWAQdG', true, true, true,
        true),
       ('manager', 'manager@example.com', '$2a$10$wYfPLLGDoiizPj9PEWEzWu/T3R0FSlPE.8VWQQZVjbIRZAGBfu7nm', true, true,
        true, true),
       ('user', 'user@example.com', '$2a$10$ZAX7SBC.IOy4FWP98WPgge8Pzw5Jh8Izj/QC1kmCk8MxRbBJt.PHW', true, true, true,
        true);

-- Insert user roles
INSERT INTO user_roles (user_id, role_id)
VALUES ((SELECT id FROM users WHERE username = 'admin'), (SELECT id FROM roles WHERE name = 'ADMIN')),
       ((SELECT id FROM users WHERE username = 'manager'), (SELECT id FROM roles WHERE name = 'MANAGER')),
       ((SELECT id FROM users WHERE username = 'user'), (SELECT id FROM roles WHERE name = 'USER'));
