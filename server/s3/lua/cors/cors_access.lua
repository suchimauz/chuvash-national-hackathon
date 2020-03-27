if ngx.req.get_method() == "OPTIONS" then
  ngx.exit(204)
end
