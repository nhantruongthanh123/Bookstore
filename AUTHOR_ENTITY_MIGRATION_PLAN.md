# Author Entity Migration Plan (from `Book.author` String)

## Goal
Move from `books.author` (string) to a dedicated `authors` table/entity, while preserving existing data and keeping API behavior stable during migration.

## Current State
- `Book` entity has `String author`.
- Search uses `BookSpecification.hasAuthor()` with `root.get("author")`.
- Liquibase creates `books.author` in `002-create-book-table.yaml`.
- Seed data inserts author names directly into `books.author` in `004-insert-sample-datas.yaml`.

---

## Phase 1: Database migration (safe, backward-compatible)

1. Create new Liquibase file: `015-create-author-table.yaml`
   - Create table `authors`:
     - `id` BIGINT PK auto-increment
     - `name` VARCHAR(255) NOT NULL
     - `description` VARCHAR(10000) NULL
   - Add unique constraint/index on `name` (or on normalized lower-case name if you enforce case-insensitive uniqueness).

2. Create new Liquibase file: `016-add-author-id-to-books.yaml`
   - Create join table `book_author(book_id, author_id)` for many-to-many relation.
   - Add PK/FKs and index on `author_id`.

3. Create new Liquibase file: `017-backfill-authors-from-books.yaml`
   - Insert distinct values from `books.author` into `authors.name`.
   - Backfill `book_author` by matching `books.author = authors.name`.
   - Validate no row lost:
     - every book with non-null old `books.author` has at least one row in `book_author`.

4. Create new Liquibase file: `018-enforce-author-id-not-null.yaml`
   - Enforce backfill integrity with preconditions and add supporting index on `book_id`.
   - Keep old `books.author` column temporarily for rollback compatibility.

5. Create new Liquibase file: `019-drop-legacy-author-column.yaml` (deferred cleanup)
   - Remove `books.author` only after application code and clients no longer rely on legacy column.

6. Register `015` -> `018` in `db.changelog-master.yaml` now. Keep `019` for later release.

---

## Phase 2: Entity and repository changes

1. Add `Author` entity
   - Path: `src/main/java/com/bookstore/entity/Author.java`
   - Fields: `id`, `name`
   - Optional reverse relation: `@ManyToMany(mappedBy = "authors") Set<Book> books` (only if needed).

2. Update `Book` entity
   - Replace `String author` with multi-author relation:
     - `@ManyToMany`
     - `@JoinTable(name = "book_author", joinColumns = ..., inverseJoinColumns = ...)`
     - `private Set<Author> authors;`

3. Add `AuthorRepository`
   - Path: `src/main/java/com/bookstore/repository/AuthorRepository.java`
   - Methods:
     - `Optional<Author> findByNameIgnoreCase(String name)`
     - Search helper methods as needed.

4. Update `BookRepository` `@EntityGraph`
   - Include `authors` where needed to avoid N+1 when mapping to DTO.

---

## Phase 3: DTO + Mapper contract (preserve API)

1. Transition DTOs with minimal breakage:
   - Create `AuthorRequest` and `AuthorResponse` DTOs.
   - `BookResponse`:
      - Return `Set<AuthorResponse> authors`.
   - `BookRequest`:
      - Accept `Set<Long> authorsIds` from frontend.

2. Update `BookMapper`
   - Map `book.authors -> response.authors` via `AuthorMapper`.
   - Do not rely only on automatic MapStruct for entity lookup; resolve `Author` in service layer.

3. Update `BookServiceImpl`
   - On create/update:
      - Resolve each `authorsIds` item by id and assign `book.setAuthors(...)`.

---

## Phase 4: Author module APIs

1. Create `AuthorService` + `AuthorServiceImpl`
   - `getAuthors(find, pageable)` for search + pagination.
   - `getAuthorById(id)`.
   - `createAuthor(request)`.
   - `updateAuthor(id, request)`.
   - `deleteAuthor(id)` with guard when author is assigned to books.

2. Create `AuthorController` endpoints
   - User/Admin:
     - `GET /api/authors` (find + pagination)
     - `GET /api/authors/{id}`
   - Admin only (`@PreAuthorize("hasRole('ADMIN')")`):
     - `POST /api/admin/authors`
     - `PATCH /api/admin/authors/{id}`
     - `DELETE /api/admin/authors/{id}`

3. Keep book search by author name
   - `BookSpecification.hasAuthor()` joins `authors` and filters by `name`.

---

## Phase 5: Data conservation checklist

1. Before migration:
   - Backup DB or snapshot.
   - Record counts:
     - total books
     - distinct `books.author`

2. After backfill:
   - `authors` count == distinct old author names.
   - all books with legacy `books.author` have at least one row in `book_author`.
   - sampled books keep same visible author text through API.

3. Before dropping old column:
   - Confirm all reads/writes use relation (`book.authors` / `book_author`).
   - Confirm no Liquibase/seed file still writes to `books.author`.

---

## Suggested execution order

1. Liquibase 015-018 (create + backfill + join table migration)
2. Entity/repository/service/mapper updates
3. Search specification update
4. API compatibility pass (DTO contract)
5. Liquibase 019 cleanup (drop old column) in a later release

