# Agent Configuration

## Purpose
This repository defines a payments-platform reference implementation. Agents interacting with this codebase must follow the guidelines in this document to ensure a consistent tooling setup and deterministic behaviour.

## Locked MCP Configuration
Use the Model Context Protocol (MCP) in locked mode so every invocation loads the same server definitions. Do **not** override or extend this list when working in this repository.

```json
{
  "locked": true,
  "servers": {
    "context7": {
      "type": "http",
      "url": "https://context7.modelcontextprotocol.io"
    }
  }
}
```

## Usage Notes
- Always establish the MCP connection to the `context7` server before attempting to fetch runtime documentation or SDK references.
- Treat the configuration above as authoritative. If additional MCP servers are required, open a pull request that amends this document rather than introducing ad-hoc changes.
- Persist this file at the repository root so other contributors and automation can locate the canonical MCP configuration quickly.
