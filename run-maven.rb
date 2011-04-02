#!/usr/bin/env ruby

if ARGV.empty?
	puts "utilisation: #{$0} <cible>[ <cible>[...]]"
	exit
end

projets = ["mongolink-parent", "mongolink-testtools", "mongolink"]

projets.each do |projet|
	Dir.chdir(projet)
	if not system "mvn #{ARGV.join(" ")}"
		exit
	end
	Dir.chdir("..")
end
