import {schema, defaultMarkdownParser, MarkdownParser, MarkdownSerializer, defaultMarkdownSerializer, MarkdownSerializerState} from "prosemirror-markdown"

module.exports = {
    MarkdownParser: MarkdownParser,
    defaultMarkdownParser: defaultMarkdownParser,
    defaultMarkdownSerializer: defaultMarkdownSerializer,
    MarkdownSerializerState: MarkdownSerializerState,
    MarkdownSerializer: MarkdownSerializer,
    schema: schema
};