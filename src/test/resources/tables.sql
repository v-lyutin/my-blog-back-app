CREATE TABLE IF NOT EXISTS my_blog.posts (
  id BIGSERIAL PRIMARY KEY,
  title VARCHAR(64) NOT NULL,
  text VARCHAR(4096) NOT NULL,
  likes_count BIGINT NOT NULL DEFAULT 0,
  comments_count BIGINT NOT NULL DEFAULT 0,
  CONSTRAINT posts_likes__non_negative CHECK (likes_count >= 0),
  CONSTRAINT posts_comments__non_negative CHECK (comments_count >= 0)
);

CREATE TABLE IF NOT EXISTS my_blog.tags (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(64) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS my_blog.post_tag (
  post_id BIGINT NOT NULL REFERENCES my_blog.posts(id) ON DELETE CASCADE,
  tag_id BIGINT NOT NULL REFERENCES my_blog.tags(id)  ON DELETE CASCADE,
  PRIMARY KEY (post_id, tag_id)
);

CREATE TABLE IF NOT EXISTS my_blog.comments (
  id BIGSERIAL PRIMARY KEY,
  text VARCHAR(4096)   NOT NULL,
  post_id BIGINT NOT NULL REFERENCES my_blog.posts(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS my_blog.post_images (
  post_id BIGINT PRIMARY KEY REFERENCES my_blog.posts(id) ON DELETE CASCADE,
  data BYTEA NOT NULL
);