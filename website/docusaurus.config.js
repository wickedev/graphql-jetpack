// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

const lightCodeTheme = require('prism-react-renderer/themes/github');
const darkCodeTheme = require('prism-react-renderer/themes/dracula');

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'GraphQL Jetpack',
  tagline: 'A collection of packages for easily writing Kotlin GraphQL server implementations',
  url: 'https://wickedev.github.io',
  baseUrl: '/graphql-jetpack/',
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',
  favicon: 'img/favicon.ico',
  organizationName: 'wickedev', // Usually your GitHub org/user name.
  projectName: 'graphql-jetpack', // Usually your repo name.

  presets: [
    [
      'classic',
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          routeBasePath: '/',
          sidebarPath: require.resolve('./sidebars.js'),
          // Please change this to your repo.
          // editUrl: 'https://github.com/facebook/docusaurus/tree/main/packages/create-docusaurus/templates/shared/',
        },
        blog: false,
        pages: false,
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
      }),
    ],
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      navbar: {
        title: 'GraphQL Jetpack',
        logo: {
          alt: 'GraphQL Jetpack Logo',
          src: 'img/logo.svg',
        },
        items: [
          {
            href: 'https://github.com/wickedev/graphql-jetpack',
            label: 'GitHub',
            position: 'right',
          },
        ],
      },
      footer: {
        style: 'dark',
        links: [
          {
            title: 'Docs',
            items: [
              {
                label: 'Tutorial',
                to: '/',
              },
            ],
          },
          {
            title: 'Community',
            items: [
              {
                label: 'Stack Overflow',
                href: 'https://stackoverflow.com/questions/tagged/graphql-jetpack',
              },
              {
                label: 'Discord',
                href: 'https://discordapp.com/invite/graphql-jetpack',
              },
              {
                label: 'Twitter',
                href: 'https://twitter.com/graphql-jetpack',
              },
            ],
          },
          {
            title: 'More',
            items: [
              {
                label: 'GitHub',
                href: 'https://github.com/wickedev/graphql-jetpack',
              },
            ],
          },
        ],
        copyright: `Copyright © ${new Date().getFullYear()} wickedev. Built with Docusaurus.`,
      },
      colorMode: {
        defaultMode: 'dark',
      },
      prism: {
        defaultLanguage: 'kotlin',
        additionalLanguages: ['kotlin', 'groovy'],
        theme: lightCodeTheme,
        darkTheme: darkCodeTheme,
      },
    }),
};

module.exports = config;
