import {schema, MarkdownSerializerState, MarkdownSerializer, MarkdownParser, defaultMarkdownParser, defaultMarkdownSerializer} from "prosemirror-markdown"

module.exports = {
    MarkdownParser: MarkdownParser,
    defaultMarkdownParser: defaultMarkdownParser,
    defaultMarkdownSerializer: defaultMarkdownSerializer,
    MarkdownSerializerState: MarkdownSerializerState,
    MarkdownSerializer: MarkdownSerializer,
    schema: schema
};