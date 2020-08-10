HUGO_VERSION  := "0.74.3"
HUGO_FLAVOUR  := "hugo_extended"
HUGO_BINARY   := "./hugo"
HUGO_ENV      ?= "dev"
OS            := "Linux"

ifeq ($(shell uname),Darwin)
	OS := "macOS"
endif

build public: hugo
	@ HUGO_ENV=$(HUGO_ENV) $(HUGO_BINARY)

guard-%:
	@ if [ "${${*}}" = "" ]; then \
			echo "Environment variable $* not set"; \
			exit 1; \
	fi

clean clobber:
	@ rm -fr public tmp
	@ rm -fr hugo

install hugo:
	@ mkdir -p tmp
	@ curl -LsS https://github.com/gohugoio/hugo/releases/download/v$(HUGO_VERSION)/$(HUGO_FLAVOUR)_$(HUGO_VERSION)_$(OS)-64bit.tar.gz | tar xzf - hugo
	@ rm -rf tmp

run develop: hugo
	@ HUGO_ENV=$(HUGO_ENV) $(HUGO_BINARY) serve -D


.PHONY: guard-% clean clobber install develop run public
