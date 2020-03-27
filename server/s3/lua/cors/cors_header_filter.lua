local request_method = ngx.req.get_method()

if request_method == 'GET' or request_method == 'POST' or request_method == 'OPTIONS' then

  local function set_header(header_name, new_value, default_value)
    if new_value then
      ngx.header[header_name] = new_value
    else
      ngx.header[header_name] = default_value
    end
  end

  set_header('Access-Control-Allow-Origin', ngx.var.http_origin, '*')
  set_header('Access-Control-Allow-Credentials', ngx.var.access_control_allow_credentials, 'true')

  if request_method == 'OPTIONS' then
    set_header('Access-Control-Allow-Methods', ngx.var.access_control_allow_methods, 'GET, POST, OPTIONS')
    set_header('Access-Control-Allow-Headers', ngx.var.access_control_allow_headers, 'Authorization,Content-Type,Accept,Origin,User-Agent,DNT,Cache-Control,X-Mx-ReqToken,Keep-Alive,X-Requested-With,If-Modified-Since')
    set_header('Access-Control-Expose-Headers', ngx.var.access_control_expose_headers, 'Content-Length')
    set_header('Access-Control-Max-Age', ngx.var.access_control_max_age, '86400')

    if ngx.var.access_control_allow_headers_append then
      ngx.header['Access-Control-Allow-Headers'] = ngx.header['Access-Control-Allow-Headers'] .. ',' .. ngx.var.access_control_allow_headers_append
    end

    ngx.header['Content-Length'] = '0'
    ngx.header['Content-Type'] = 'text/plain charset=UTF-8'
  end
end
