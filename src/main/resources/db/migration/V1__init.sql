-- V1__init_trello_schema.sql
-- Flyway migration for Trello-like schema

-- ======================
-- ENUMS
-- ======================
CREATE TYPE role_name_enum AS ENUM (
    'ADMIN',
    'MEMBER',
    'VIEWER'
);

-- ======================
-- TABLES
-- ======================

CREATE TABLE roles
(
    id   INT PRIMARY KEY NOT NULL,
    name role_name_enum  NOT NULL UNIQUE
);

CREATE TABLE images
(
    id  INT PRIMARY KEY NOT NULL,
    key TEXT            NOT NULL
);

INSERT INTO roles (id, name)
VALUES (1, 'ADMIN'),
       (2, 'MEMBER'),
       (3, 'VIEWER');

INSERT INTO images (id, key)
VALUES (1, '/images/board_background/background_1.jpg'),
       (2, '/images/board_background/background_2.jpg'),
       (3, '/images/board_background/background_3.jpg'),
       (4, '/images/board_background/background_4.jpg'),
       (5, '/images/board_background/background_5.jpg'),
       (6, '/images/board_background/background_6.jpg'),
       (7, '/images/board_background/background_7.jpg'),
       (8, '/images/board_background/background_8.jpg');

-- ======================
-- User
-- ======================
CREATE TABLE "users"
(
    id         UUID PRIMARY KEY NOT NULL,
    cognito_id VARCHAR(255)     NOT NULL,
    full_name  VARCHAR(255)     NOT NULL,
    email      VARCHAR(255)     NOT NULL,
    avatar_url VARCHAR(255),
    bio        TEXT,
    phone      VARCHAR(255),
    address    VARCHAR(255),
    deleted_at TIMESTAMP,
    created_at TIMESTAMP        NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP        NOT NULL DEFAULT NOW()
);

-- ======================
-- WorkspaceCategory
-- ======================
CREATE TABLE workspace_categories
(
    id   INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

INSERT INTO workspace_categories (id, name)
VALUES (1, 'Sales CRM'),
       (2, 'Education'),
       (3, 'Human Resources'),
       (4, 'Operation'),
       (5, 'Marketing'),
       (6, 'IT'),
       (7, 'Small Business'),
       (8, 'Other');

-- ======================
-- Workspace
-- ======================
CREATE TABLE workspaces
(
    id          UUID PRIMARY KEY NOT NULL,
    name        VARCHAR(255)     NOT NULL,
    description VARCHAR(255),
    category_id INT              NOT NULL,
    created_at  TIMESTAMP        NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP        NOT NULL DEFAULT now(),
    created_by  UUID             NOT NULL REFERENCES "users" (id) ON DELETE SET NULL,
    updated_by  UUID             REFERENCES "users" (id) ON DELETE SET NULL,
    CONSTRAINT fk_workspace_category
        FOREIGN KEY (category_id) REFERENCES workspace_categories (id)
);

-- ======================
-- Board
-- ======================
CREATE TABLE boards
(
    id             UUID PRIMARY KEY NOT NULL,
    name           VARCHAR(255)     NOT NULL,
    background_url TEXT,
    workspace_id   UUID             NOT NULL REFERENCES workspaces (id) ON DELETE CASCADE,
    created_by     UUID             NOT NULL REFERENCES "users" (id) ON DELETE SET NULL,
    updated_by     UUID             REFERENCES "users" (id) ON DELETE SET NULL,
    created_at     TIMESTAMP        NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP        NOT NULL DEFAULT NOW()
);

-- ======================
-- List
-- ======================
CREATE TABLE lists
(
    id         UUID PRIMARY KEY NOT NULL,
    name       VARCHAR(255)     NOT NULL,
    board_id   UUID             NOT NULL REFERENCES boards (id) ON DELETE CASCADE,
    updated_by UUID             REFERENCES users (id) ON DELETE SET NULL,
    created_by UUID             NOT NULL REFERENCES users (id) ON DELETE SET NULL,
    position       TEXT             NOT NULL,
    created_at TIMESTAMP        NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP        NOT NULL DEFAULT NOW()
);

-- ======================
-- Card
-- ======================
CREATE TABLE cards
(
    id           UUID PRIMARY KEY NOT NULL,
    title        VARCHAR(255)     NOT NULL,
    description  TEXT,
    start_date   TIMESTAMP,
    due_date     TIMESTAMP,
    is_completed BOOLEAN                   DEFAULT FALSE,
    position         TEXT             NOT NULL,
    list_id      UUID             NOT NULL REFERENCES lists (id) ON DELETE CASCADE,
    created_by   UUID             NOT NULL REFERENCES users (id) ON DELETE SET NULL,
    updated_by   UUID             REFERENCES users (id) ON DELETE SET NULL,
    created_at   TIMESTAMP        NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP        NOT NULL DEFAULT NOW()
);

-- ======================
-- Checklist
-- ======================
CREATE TABLE checklists
(
    id         UUID PRIMARY KEY NOT NULL,
    name       VARCHAR(255)     NOT NULL,
    card_id    UUID             NOT NULL REFERENCES cards (id) ON DELETE CASCADE,
    position         TEXT             NOT NULL,
    created_by UUID             NOT NULL REFERENCES users (id) ON DELETE SET NULL,
    updated_by UUID             REFERENCES users (id) ON DELETE SET NULL,
    created_at TIMESTAMP        NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP        NOT NULL DEFAULT NOW()
);

-- ======================
-- ChecklistItem
-- ======================
CREATE TABLE checklist_items
(
    id           UUID PRIMARY KEY NOT NULL,
    name         VARCHAR(255)     NOT NULL,
    checklist_id UUID             NOT NULL REFERENCES checklists (id) ON DELETE CASCADE,
    position         TEXT             NOT NULL,
    is_completed BOOLEAN          NOT NULL DEFAULT FALSE,
    due_date     TIMESTAMP,
    created_at   TIMESTAMP        NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP        NOT NULL DEFAULT NOW(),
    created_by   UUID             NOT NULL REFERENCES users (id) ON DELETE SET NULL,
    updated_by   UUID             REFERENCES users (id) ON DELETE SET NULL
);

-- ======================
-- WorkspaceMember
-- ======================
CREATE TABLE workspace_members
(
    id           UUID PRIMARY KEY NOT NULL,
    user_id      UUID             NOT NULL REFERENCES "users" (id) ON DELETE CASCADE,
    workspace_id UUID             NOT NULL REFERENCES workspaces (id) ON DELETE CASCADE,
    role_id      INT              NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    created_at   TIMESTAMP        NOT NULL DEFAULT NOW(),
    created_by   UUID             NOT NULL REFERENCES users (id) ON DELETE SET NULL
);

-- ======================
-- BoardMember
-- ======================
CREATE TABLE board_members
(
    id         UUID PRIMARY KEY NOT NULL,
    user_id    UUID             NOT NULL REFERENCES "users" (id) ON DELETE CASCADE,
    board_id   UUID             NOT NULL REFERENCES boards (id) ON DELETE CASCADE,
    role_id    INT              NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    created_at TIMESTAMP        NOT NULL DEFAULT NOW(),
    created_by   UUID             NOT NULL REFERENCES users (id) ON DELETE SET NULL
);

-- ======================
-- CardMember
-- ======================
CREATE TABLE card_members
(
    id         UUID PRIMARY KEY NOT NULL,
    user_id    UUID             NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    card_id    UUID             NOT NULL REFERENCES cards (id) ON DELETE CASCADE,
    created_at TIMESTAMP        NOT NULL DEFAULT NOW(),
    created_by   UUID             NOT NULL REFERENCES users (id) ON DELETE SET NULL
);

-- ======================
-- Bổ sung constraints
-- ======================

-- 1. User
ALTER TABLE users
    ADD CONSTRAINT uq_user_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uq_user_cognito_id UNIQUE (cognito_id);

-- 2. WorkspaceMember
ALTER TABLE workspace_members
    ADD CONSTRAINT uq_workspace_member UNIQUE (user_id, workspace_id);

-- 3. BoardMember
ALTER TABLE board_members
    ADD CONSTRAINT uq_board_member UNIQUE (user_id, board_id);

-- 4. CardMember
ALTER TABLE card_members
    ADD CONSTRAINT uq_card_member UNIQUE (user_id, card_id);

-- 6. ChecklistItem: check is_completed valid
ALTER TABLE checklist_items
    ADD CONSTRAINT chk_checklistitem_is_completed CHECK (is_completed IN (true, false));