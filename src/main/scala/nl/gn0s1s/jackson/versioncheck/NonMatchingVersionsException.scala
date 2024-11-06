package nl.gn0s1s.jackson.versioncheck

case object NonMatchingVersionsException extends IllegalStateException("Non-matching Jackson module versions found")
