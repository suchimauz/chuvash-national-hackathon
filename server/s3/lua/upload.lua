local file_path = ngx.var.file_path
local file_name = ngx.var.file_name

local mkdir_path = "/var/data/storage" .. file_path
local url = "/storage/download" .. file_path .. "/" .. file_name
local new_local_file = mkdir_path .. "/" .. file_name

local mkdir = os.execute("mkdir -p " .. mkdir_path)
if not mkdir then
  ngx.status = 500
  ngx.say("mkdir: error")
  ngx.exit(ngx.OK)
  return
end
local move_file, err = os.rename(ngx.var.temp_file, new_local_file)
if err then
  ngx.status = 500
  ngx.say("rename: " .. err)
  ngx.exit(ngx.OK)
  return
end

local cjson = require "cjson"
local json = cjson.encode({
  url = url
})
ngx.status = 200
ngx.header.content_type = "application/json; charset=utf-8"
ngx.say(json)
ngx.exit(ngx.OK)
