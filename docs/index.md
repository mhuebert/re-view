[
{% for post in site.posts %}
 { "title"    : "{{ post.title | escape }}",
  "url"     : "{{ post.url }}",
  "date"     : "{{ post.date | date: "%B %d, %Y" }}",
  "content"  : "{{ post.content | escape }}"
  }
  {% if forloop.last %}{% else %},{% endif %}
{% endfor %}
]